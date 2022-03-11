@file:Suppress("UNCHECKED_CAST", "unused")

package durak.app.utils

import kotlin.collections.*
import kotlin.reflect.KClass

class StateMachine<STATE : Any, EVENT : Any, SIDE_EFFECT : Any> internal constructor(private val graph: Graph<STATE, EVENT, SIDE_EFFECT>) {
    private val lock = Lock()
    private val stateRef = AtomicReference(graph.initialState)
    val state: STATE get() = stateRef.get()
    fun transition(event: EVENT) = lock.withLock {
        state.getTransition(event).also { t -> if (t is Transition.Valid) stateRef.set(t.toState) }
    }.apply {
        notifyOnTransition()
        if (this is Transition.Valid) {
            fromState.notifyOnExit(event)
            toState.notifyOnEnter(event)
        }
    }

    fun with(init: Builder<STATE, EVENT, SIDE_EFFECT>.() -> Unit) = StateMachine(Builder(graph.copy(initialState = state)).apply(init).build())

    private fun STATE.getTransition(event: EVENT) = getDefinition().transitions.firstNotNullOfOrNull { (matcher, transitionTo) ->
        when (matcher.matches(event)) {
            true -> transitionTo(this, event).let { (toState, sideEffect) -> Transition.Valid(this, event, toState, sideEffect) }
            else -> null
        }
    } ?: Transition.Invalid(this, event)

    private fun STATE.getDefinition() = graph.stateDefinitions
        .filter { it.key.matches(this) }
        .map { it.value }
        .firstOrNull() ?: error("Missing definition for state ${this::class.simpleName}!")

    private fun STATE.notifyOnEnter(cause: EVENT) = getDefinition().onEnterListeners.forEach { it(this, cause) }

    private fun STATE.notifyOnExit(cause: EVENT) = getDefinition().onExitListeners.forEach { it(this, cause) }

    private fun Transition<STATE, EVENT, SIDE_EFFECT>.notifyOnTransition() = graph.onTransitionListeners.forEach { it(this@StateMachine, this) }

    sealed class Transition<out STATE : Any, out EVENT : Any, out SIDE_EFFECT : Any> {
        abstract val fromState: STATE
        abstract val event: EVENT

        data class Valid<out STATE : Any, out EVENT : Any, out SIDE_EFFECT : Any> internal constructor(
            override val fromState: STATE,
            override val event: EVENT,
            val toState: STATE,
            val sideEffect: SIDE_EFFECT?
        ) : Transition<STATE, EVENT, SIDE_EFFECT>()

        data class Invalid<out STATE : Any, out EVENT : Any, out SIDE_EFFECT : Any> internal constructor(
            override val fromState: STATE,
            override val event: EVENT
        ) : Transition<STATE, EVENT, SIDE_EFFECT>()
    }

    data class Graph<STATE : Any, EVENT : Any, SIDE_EFFECT : Any>(
        val initialState: STATE,
        val stateDefinitions: Map<Matcher<STATE, STATE>, State<STATE, EVENT, SIDE_EFFECT>>,
        val onTransitionListeners: List<StateMachine<STATE, EVENT, SIDE_EFFECT>.(Transition<STATE, EVENT, SIDE_EFFECT>) -> Unit>
    ) {
        class State<STATE : Any, EVENT : Any, SIDE_EFFECT : Any> internal constructor() {
            val transitions = linkedMapOf<Matcher<EVENT, EVENT>, (STATE, EVENT) -> Pair<STATE, SIDE_EFFECT?>>()
            val onEnterListeners = mutableListOf<(STATE, EVENT) -> Unit>()
            val onExitListeners = mutableListOf<(STATE, EVENT) -> Unit>()
        }
    }

    class Matcher<T : Any, out R : T> private constructor(private val clazz: KClass<R>) {
        private val predicates = mutableListOf<(T) -> Boolean>({ clazz.isInstance(it) })

        fun where(predicate: R.() -> Boolean): Matcher<T, R> = apply {
            predicates.add { (it as R).predicate() }
        }

        fun matches(value: T) = predicates.all { it(value) }

        companion object {
            fun <T : Any, R : T> any(clazz: KClass<R>) = Matcher<T, R>(clazz)

            inline fun <T : Any, reified R : T> any() = any<T, R>(R::class)

            inline fun <T : Any, reified R : T> eq(value: R) = any<T, R>().where { this == value }
        }
    }

    class Builder<STATE : Any, EVENT : Any, SIDE_EFFECT : Any>(graph: Graph<STATE, EVENT, SIDE_EFFECT>? = null) {
        private var initialState = graph?.initialState

        private val stateDefinitions = LinkedHashMap(graph?.stateDefinitions ?: emptyMap())

        private val onTransitionListeners = ArrayList(graph?.onTransitionListeners ?: emptyList())

        fun initialState(state: STATE) = apply { initialState = state }

        fun <S : STATE> state(
            matcher: Matcher<STATE, S>,
            init: StateDefinitionBuilder<S>.() -> Unit
        ) = stateDefinitions.set(matcher, StateDefinitionBuilder<S>().apply(init).build())

        inline fun <reified S : STATE> state(
            noinline init: StateDefinitionBuilder<S>.() -> Unit
        ) = state(Matcher.any(), init)

        inline fun <reified S : STATE> state(
            state: S,
            noinline init: StateDefinitionBuilder<S>.() -> Unit
        ) = state(Matcher.eq(state), init)

        fun onTransition(
            listener: StateMachine<STATE, EVENT, SIDE_EFFECT>.(Transition<STATE, EVENT, SIDE_EFFECT>) -> Unit
        ) = onTransitionListeners.add(listener)

        fun build() = Graph(requireNotNull(initialState), stateDefinitions.toMap(), onTransitionListeners.toList())

        inner class StateDefinitionBuilder<S : STATE> {
            @PublishedApi
            internal val it = Graph.State<STATE, EVENT, SIDE_EFFECT>()

            fun build() = it

            inline fun <reified E : EVENT> any() = Matcher.any<EVENT, E>()

            inline fun <reified E : EVENT> eq(value: E) = Matcher.eq<EVENT, E>(value)

            fun onEnter(listener: S.(EVENT) -> Unit) = it.onEnterListeners.add { state, cause -> listener(state as S, cause) }

            fun onExit(listener: S.(EVENT) -> Unit) = it.onExitListeners.add { state, cause -> listener(state as S, cause) }

            inline fun <reified E : EVENT> on(noinline transitionTo: S.(E) -> Pair<STATE, SIDE_EFFECT?>) = on(any(), transitionTo)

            inline fun <reified E : EVENT> on(event: E, noinline transitionTo: S.(E) -> Pair<STATE, SIDE_EFFECT?>) = on(eq(event), transitionTo)

            inline fun <reified E : EVENT> on(matcher: Matcher<EVENT, E>, noinline transitionTo: S.(E) -> Pair<STATE, SIDE_EFFECT?>) =
                it.transitions.set(matcher) { state, event -> transitionTo(state as S, event as E) }
        }
    }
}

fun <STATE : Any, EVENT : Any, SIDE_EFFECT : Any> createStateMachine(
    state: STATE,
    graph: StateMachine.Graph<STATE, EVENT, SIDE_EFFECT>? = null,
    init: StateMachine.Builder<STATE, EVENT, SIDE_EFFECT>.() -> Unit
) = StateMachine(StateMachine.Builder(graph).apply {
    initialState(state)
    init()
}.build())
