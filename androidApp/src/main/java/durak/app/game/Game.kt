package durak.app.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import durak.app.bluetooth.Socket
import durak.app.config.Config
import durak.app.config.slots
import durak.app.utils.fastForEach
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

interface Game {
    val plrId: PlayerId
    val currentState: Value<State>
    val effectFlow: MutableSharedFlow<Effect>
    val cards: Cards
    val rules: Rules
    val table: Table
    val isMenu: Value<Boolean>
    val infos: List<PlayerInfo>
    val players: List<Player>
    val slots: Set<HandSlot>
    fun transition(state: Action)
    fun onMenuClick() {}
    fun onRestartClick() {}
    fun onDoneClick() {}
    fun onCardClick(card: Card) {}
    fun onCardDrop(card: Card, it: Pair<Float, Float>) {}
    fun slotOf(plr: PlayerId): HandSlot
    suspend fun computeEffect(effect: Effect)
    fun save() {}
}

class GamePreview(screenWidthDp: Float, screenHeightDp: Float, numPlayers: Int = 3) : Game {
    override val plrId: Int = 0
    override val slots = Config.defaultValue.slots(plrId, numPlayers, screenWidthDp / screenHeightDp)
    override val rules = Rules()
    override val table = Table.initial(screenWidthDp, screenHeightDp, 1f)
    override val cards = allCards
    private val state = GameState(cards, rules, (0 until numPlayers).map { cards.hand(it, rules) }, table = Table.initial(screenWidthDp, screenHeightDp, 1f))
    override val infos = (0 until numPlayers).map { PlayerInfo(null, "Foo", "Bar") }
    override val players = players(numPlayers, plrId)
    override val currentState = MutableValue(state)
    override val effectFlow = MutableSharedFlow<Effect>(1, numPlayers, DROP_OLDEST).apply { tryEmit(NewRoundEffect) }
    override val isMenu: Value<Boolean> = MutableValue(false)
    override fun transition(state: Action) {}
    override fun slotOf(plr: PlayerId) = slots.elementAt(plr)
    override suspend fun computeEffect(effect: Effect) {}

    init {
        state.updateAllStates(::slotOf)
    }
}

abstract class GameComponent(componentContext: ComponentContext) : Game, ComponentContext by componentContext {
    override val isMenu = MutableValue(false)

    override fun slotOf(plr: PlayerId) = slots.elementAt(plr)
    override fun onMenuClick() = isMenu.reduce { !it }
    override fun onDoneClick() = transition(SetDoneAction(plrId))
    override fun onRestartClick() = transition(NewRoundAction(0)).also { isMenu.value = false }
    override fun onCardClick(card: Card) = (currentState.value as GameState).run {
        println("onclick $card")
        if (isTurn && undone.contains(plrId)) {
            val pairs = table.openCards.map { it to card } + (card to null)
            pairs.firstOrNull { (a, b) -> canPutCard(plrId, a, b, rules) }?.let { transition(PutCardAction(plrId, it)) }
        }
    }

    override fun onCardDrop(card: Card, it: Pair<Float, Float>) = (currentState.value as GameState).run {
        val hand = handOf(plrId)
        if (hand.contains(card)) {
            val pos = slotOf(plrId).position(hand.indexOf(card), hand.size, rules.handSize)
            val (dx, dy) = ((slotOf(plrId).direction + slotOf(plrId).angleOf(hand.indexOf(card), hand.size, rules.handSize)) * PI.toFloat() / 180f)
                .let { z -> (it.first * cos(z) - it.second * sin(z)) * pos.scale + pos.offsetXdp to (it.first * sin(z) + it.second * cos(z)) * pos.scale + pos.offsetYdp }
            replaceStateOf(card) { pos.copy(offsetXdp = dx, offsetYdp = dy) }
            val card2 = table.closestCard(card)?.takeIf { it.value < CardWidthDp && canPutCard(plrId, it.key, card, rules) && plrId == def }?.key
            when {
                card2 != null -> transition(PutCardAction(plrId, card2 to card))
                dy < -CardHeightDp && canPutCard(plrId, card, null, rules) -> transition(PutCardAction(plrId, card to null))
                else -> updateStateOf(card) { pos }
            }
        }
    }
}

class LocalGameComponent(
    private val io: CoroutineDispatcher,
    override val plrId: PlayerId,
    override val cards: Cards,
    override val infos: List<PlayerInfo>,
    override val players: List<Player>,
    override val slots: Set<HandSlot>,
    override val rules: Rules = Rules(),
    override val table: Table,
    initialState: State = NoState,
    componentContext: ComponentContext,
) : GameComponent(componentContext) {
    override val currentState = MutableValue(initialState)
    override val effectFlow = MutableSharedFlow<Effect>(1, players.size, DROP_OLDEST)
    private val stateMachine = stateMachine(initialState, currentState, effectFlow, EnterGameAction(cards, players.size, rules, table.empty()))
    override fun transition(state: Action) = stateMachine.transition(state).run { }

    @Suppress("unused")
    private val handler = instanceKeeper.getOrCreate("Handler") { LocalGameHandler(this, io) }

    init {
        backPressedHandler.register { false }
        lifecycle.doOnDestroy {
            handler.onDestroy()
            instanceKeeper.remove("Handler")
        }
    }

    override suspend fun computeEffect(effect: Effect) {
        if (currentState.value is GameState && effect.interactive) {
            delay(500)
            (currentState.value as GameState).run {
                if (isTurn) for (plr in setOf(def, att, *(active - att - def).toTypedArray())) {
                    val action = players.elementAt(plr).let { it as? AiPlayer }?.run {
                        val cards = handOf(id)
                        when {
                            !isTurn || !undone.contains(id) -> null

                            id == def && undone.contains(id) && table.openCards.isNotEmpty() -> table.openCards.firstNotNullOfOrNull { card -> cards.firstOrNull { canPutCard(id, card, it, rules) }?.let { card to it } }?.let { PutCardAction(id, it) } ?: SetDoneAction(id)

                            id == att && table.cards.isEmpty() -> cards.firstOrNull()?.let { PutCardAction(id, it to null) } ?: SetDoneAction(id)

                            id != def && table.cards.isNotEmpty() -> (if (cards.all { it.suit == trump }) cards else cards.filter { it.suit != trump }).firstOrNull { canPutCard(id, it, null, rules) }?.let { PutCardAction(id, it to null) } ?: SetDoneAction(id)

                            else -> null
                        }
                    }
                    if (action != null) {
                        transition(action)
                        break
                    }
                }
            }
        }
    }
}

class RemoteGameComponent(
    private val socket: Socket,
    private val io: CoroutineDispatcher,
    override val plrId: PlayerId,
    override val cards: Cards,
    override val infos: List<PlayerInfo>,
    override val players: List<Player>,
    override val slots: Set<HandSlot>,
    override val rules: Rules = Rules(),
    override val table: Table,
    initialState: State = NoState,
    componentContext: ComponentContext,
) : GameComponent(componentContext) {
    override var currentState = MutableValue(initialState)
    override val effectFlow = MutableSharedFlow<Effect>(1, players.size, DROP_OLDEST)
    override fun transition(state: Action) {
        handler.launch {
            socket.writeNullTerminated(io, Json.encodeToString(state)) { /* possibly closed */ }
        }
    }

    @Suppress("unused")
    private val handler = instanceKeeper.getOrCreate("Handler") { RemoteGameHandler(this, io) }

    init {
        backPressedHandler.register { false }
        lifecycle.doOnDestroy {
            handler.onDestroy()
            instanceKeeper.remove("Handler")
        }
    }

    fun updateState(event: GameServerEvent) {
        currentState.reduce { event.state }
        effectFlow.tryEmit(event.effect)
    }

    override suspend fun computeEffect(effect: Effect) {
        currentState.value.executeEffect(effect, ::slotOf)
    }
}

class LocalGameHandler(game: LocalGameComponent, io: CoroutineDispatcher) : InstanceKeeper.Instance, CoroutineScope by MainScope() {
    init {
        val remotes = game.players.filterIsInstance<RemotePlayer>()
        launch(io) {
            game.effectFlow.collect { effect ->
                when {
                    effect is EnterGameEffect -> remotes.fastForEach {
                        it.socket.writeNullTerminated(io, Json.encodeToString<ServerEvent>(EnterGameServerEvent(game.currentState.value as GameState, it.id))) {}
                    }
                    effect is LeaveGameEffect -> remotes.fastForEach {
                        it.socket.writeNullTerminated(io, Json.encodeToString<ServerEvent>(LeaveGameServerEvent)) {}
                    }
                    game.currentState.value is GameState -> remotes.fastForEach {
                        it.socket.writeNullTerminated(io, Json.encodeToString<ServerEvent>(GameServerEvent(game.currentState.value as GameState, effect))) {}
                    }
                }
                game.currentState.value.executeEffect(effect, game::slotOf)?.let { action -> game.transition(action) }
            }
        }
        launch(io) {
            game.effectFlow.collect { effect ->
                game.computeEffect(effect)
            }
        }
    }

    override fun onDestroy() = cancel()
}

class RemoteGameHandler(game: RemoteGameComponent, io: CoroutineDispatcher) : InstanceKeeper.Instance, CoroutineScope by MainScope() {
    init {
        launch(io) {
            game.effectFlow.collect { effect ->
                game.computeEffect(effect)
            }
        }
    }

    override fun onDestroy() = cancel()
}
