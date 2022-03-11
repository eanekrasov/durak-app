package durak.app.game

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import durak.app.utils.StateMachine
import durak.app.utils.createStateMachine
import durak.app.utils.fastForEachIndexed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Serializable

// region etc

typealias PlayerId = Int

fun GameState.canPutCard(plr: PlayerId, card: Card, by: Card?, rules: Rules) = when {
    !isTurn || !handOf(plr).contains(by ?: card) || !undone.contains(plr) -> false // essential checks
    by != null && !table.openCards.contains(card) -> false // beating unknown card
    plr != def && by != null -> false // beating being not def
    plr == att && table.cards.isEmpty() -> true // first attack
    plr == def && by != null -> beats(card, by, trump) // just beating card
    table.cards.size == (if (rules.unlimitedAttack) -1 else if (pile.isEmpty() && rules.firstAttack5cards) rules.handSize - 1 else rules.handSize) -> false // table is full
    plr == def && by == null -> rules.switchTurn && handOf(active.nextSibling(def)).size > table.openCards.size && table.cards.all { (a, b) -> b == null && a.rank == card.rank } // switch turn rule
    handOf(def).size <= table.openCards.size -> false // not enough cards
    rules.neighbors && active.nextSibling(def) != plr && active.nextSibling(plr) != def -> false // neighbors only rule
    table.allCards.all { it.rank != card.rank } -> false // no same rank
    else -> true
}

fun GameState.canSetDone(plr: PlayerId) = when {
    !isTurn || !undone.contains(plr) -> false // essential checks
    table.cards.isEmpty() -> false
    plr == def && table.openCards.isEmpty() -> false
    else -> true
}

fun GameState.canEndTurn(rules: Rules) = when {
    !isTurn -> false
    (table.cards.size < rules.handSize || rules.unlimitedAttack) && !undone.all { plr -> handOf(plr).none { canPutCard(plr, it, null, rules) } } -> false
    table.openCards.isNotEmpty() && undone.contains(def) -> false
    else -> true
}

fun GameState.handOf(plr: PlayerId) = hands.elementAt(plr)

fun <T> Set<T>.nextSibling(anchor: T, shift: Int = 1) = elementAt((indexOf(anchor) + shift) % size)

fun <T> List<T>.indices(predicate: (T) -> Boolean) = filter(predicate).map { indexOf(it) }.toSet()

fun <T> List<T>.indicesIndexed(predicate: (Int, T) -> Boolean) = filterIndexed(predicate).map { indexOf(it) }.toSet()

// endregion

// region State

sealed interface State

@Serializable
object NoState : State

@Serializable
data class GameState(
    val cards: Cards,
    val rules: Rules,
    val hands: List<Cards>,
    val att: PlayerId = 0,
    val def: PlayerId = 1,
    val active: Set<PlayerId> = hands.indices { it.isNotEmpty() },
    val pile: Cards = listOf(),
    val talon: Int = cards.size - hands.sumOf { it.size },
    val trump: Suit? = if (rules.trumpless) null else cards.first().suit,
    val isTurn: Boolean = false,
    val undone: Set<PlayerId> = active,
    val table: Table,// = Table(newTableStates(ratio = screenWidthDp / screenHeightDp))
) : State

// endregion

// region actions

@Serializable
sealed class Action

@Serializable
sealed class Effect(val interactive: Boolean = false)

@Serializable
class JoinAction(val body1: String, val body2: String, val avatar: String? = null) : Action()

@Serializable
class EnterGameAction(val cards: Cards, val numPlayers: Int, val rules: Rules, val table: Table) : Action()

@Serializable
class EnterGameEffect(val newRound: Boolean) : Effect()

@Suppress("unused")
fun NoState.enterGame(e: EnterGameAction) = GameState(e.cards, e.rules, (0 until e.numPlayers).map { listOf() }, table = e.table.empty()) to EnterGameEffect(true)

fun GameState.enterGame() = this to EnterGameEffect(false)

fun GameState.afterEnterGame(e: EnterGameEffect, slotOf: (PlayerId) -> HandSlot) = run {
    updateAllStates(slotOf)
    if (e.newRound) NewRoundAction(0) else null
}

@Serializable
object LeaveGameAction : Action()

@Serializable
object LeaveGameEffect : Effect()

@Suppress("unused")
fun GameState.leaveGame() = NoState to LeaveGameEffect
fun NoState.leaveGame() = this to LeaveGameEffect
fun GameState.afterLeaveGame() = run { null }

@Serializable
class NewRoundAction(val att: PlayerId) : Action()

@Serializable
object NewRoundEffect : Effect()

fun GameState.newRound(a: NewRoundAction) = cards.shuffled().let { cards ->
    GameState(cards, rules, hands.indices.map { plr -> cards.hand(plr, rules) }, a.att, (a.att + 1) % hands.size, table = table.empty()) to NewRoundEffect
}

suspend fun GameState.afterNewRound(slotOf: (PlayerId) -> HandSlot) = run {
    updateAllStates(slotOf, rules, 100)
    NewTurnAction
}

@Serializable
object NewTurnAction : Action()

@Serializable
object NewTurnEffect : Effect(true)

fun GameState.newTurn() = copy(isTurn = true, table = table.empty(), undone = active) to NewTurnEffect

@Serializable
data class PutCardAction(val plr: PlayerId, val card: Pair<Card, Card?>) : Action()

@Serializable
data class PutCardEffect(val plr: PlayerId, val card: Pair<Card, Card?>) : Effect(true)

fun GameState.putCard(a: PutCardAction) = when {
    canPutCard(a.plr, a.card.first, a.card.second, rules) -> copy(
        hands = hands.mapIndexed { p, it -> if (p == a.plr) it.minus(a.card.second ?: a.card.first) else it },
        table = table.withCards(a.card.first, a.card.second),
        undone = if (a.plr == def) active else undone + a.plr,
        att = if (a.plr == def && a.card.second == null) def else att,
        def = if (a.plr == def && a.card.second == null) active.nextSibling(def) else def
    ) to PutCardEffect(a.plr, a.card)
    else -> this to null
}

suspend fun GameState.afterPutCard(effect: PutCardEffect, slotOf: (PlayerId) -> HandSlot) = run {
    updateStateOf(effect.card.second ?: effect.card.first) { table[table.cards.keys.indexOf(effect.card.first)] }
    updateHandStates(::handOf, slotOf, rules, effect.plr)
    if (canEndTurn(rules)) EndTurnAction(!undone.contains(def)).also { delay(500) } else null
}

@Serializable
data class SetDoneAction(val plr: PlayerId) : Action()

@Serializable
data class SetDoneEffect(val plr: PlayerId) : Effect(true)

fun GameState.setDone(a: SetDoneAction) = if (canSetDone(a.plr)) copy(undone = undone - a.plr) to SetDoneEffect(a.plr) else this to null

suspend fun GameState.afterSetDone() = if (canEndTurn(rules)) EndTurnAction(!undone.contains(def)).also { delay(500) } else null

@Serializable
data class EndTurnAction(val taking: Boolean) : Action()

@Serializable
data class EndTurnEffect(val table: Cards, val taking: Boolean, val updates: Map<PlayerId, Cards> = mapOf(), val def: PlayerId = 0) : Effect()

fun GameState.endTurn(a: EndTurnAction): Pair<GameState, Effect> {
    var left = talon
    val order = setOf(att, *(active - att - def).toTypedArray(), def)
    val plrs = order.associateWith { plr ->
        if (plr == def && a.taking) table.allCards else {
            val num = (rules.handSize - handOf(plr).size).coerceIn(0, left)
            left -= num
            cards.drop(left).take(num)
        }
    }
    val newHands = hands.mapIndexed { plr, cards -> (cards + plrs.getOrElse(plr) { listOf() }).sorted(if (rules.trumpless) null else trump) }
    val prev = if (a.taking) def else att
    val newActive = newHands.indicesIndexed { plr, it -> it.isNotEmpty() || plr == prev }
    val newNotEmpty = newHands.indices { it.isNotEmpty() }
    return copy(
        table = table.empty(),
        isTurn = false,
        hands = newHands,
        pile = if (a.taking) pile else pile + table.allCards,
        att = if (newActive.isEmpty()) att else newActive.nextSibling(prev, 1),
        def = if (newActive.isEmpty()) def else newActive.nextSibling(prev, 2),
        active = newNotEmpty,
        undone = newNotEmpty,
        talon = left
    ) to EndTurnEffect(table.allCards, a.taking, plrs, def)
}

suspend fun GameState.afterEndTurn(effect: EndTurnEffect, slotOf: (PlayerId) -> HandSlot) = run {
    if (effect.taking) {
        updateHandStates(::handOf, slotOf, rules, effect.def, handOf(effect.def) - effect.table)
        updateHandStates(::handOf, slotOf, rules, effect.def, effect.table, 100)
    } else {
        updatePileStates(pile::indexOf, effect.table, 20)
    }
    effect.updates.forEach { (plr, new) ->
        updateHandStates(::handOf, slotOf, rules, plr, handOf(plr) - new)
        updateHandStates(::handOf, slotOf, rules, plr, new, 100)
    }
    if (active.size < 2) EndRoundAction else NewTurnAction
}

@Serializable
object EndRoundAction : Action()

@Serializable
object EndRoundEffect : Effect()

fun GameState.endRound() = this to EndRoundEffect

fun GameState.afterEndRound() = NewRoundAction(active.firstOrNull()?.let { (it + if (rules.nextRoundAfter) 1 else hands.size - 1) % hands.size } ?: 0)

// endregion

// region effects

fun GameState.updateAllStates(slotOf: (PlayerId) -> HandSlot) {
    updateTalonStates(cards.take(talon), rules.trumpless)
    updatePileStates(pile::indexOf, pile)
    hands.fastForEachIndexed { plr, hand -> updateHandStates(::handOf, slotOf, rules, plr, hand) }
    updateTableStates(table)
}

suspend fun GameState.updateAllStates(slotOf: (PlayerId) -> HandSlot, rules: Rules, delay: Long) {
    updatePileStates(pile::indexOf, pile, delay / 5)
    updateTalonStates(cards - pile, rules.trumpless, delay / 10)
    hands.fastForEachIndexed { plr, hand -> updateHandStates(::handOf, slotOf, rules, plr, hand, delay) }
    updateTableStates(table, delay)
}

fun updateTalonStates(cards: Cards, trumpless: Boolean) = cards.fastForEachIndexed { idx, card -> updateStateOf(card) { talonSlot.position(idx, trumpless) } }

suspend fun updateTalonStates(cards: Cards, trumpless: Boolean, delay: Long) = cards.fastForEachIndexed { idx, card -> updateStateOf(card, delay) { talonSlot.position(idx, trumpless) } }

fun updatePileStates(pileIndexOf: (Card) -> Int, cards: Cards) = cards.forEach { card -> updateStateOf(card) { pileSlot.position(pileIndexOf(card)) } }

suspend fun updatePileStates(indexOf: (Card) -> Int, cards: Cards, delay: Long) = cards.forEach { card -> updateStateOf(card, delay) { pileSlot.position(indexOf(card)) } }

fun updateTableStates(table: Table) = table.cards.entries.forEachIndexed { idx, (card, by) ->
    updateStateOf(card) { table[idx] }
    if (by != null) updateStateOf(by) { table[idx] }
}

suspend fun updateTableStates(table: Table, delay: Long) = table.cards.entries.forEachIndexed { idx, (card, by) ->
    updateStateOf(card) { table[idx] }
    delay(delay)
    if (by != null) updateStateOf(by) { table[idx] }
    delay(delay)
}

fun updateHandStates(handOf: (PlayerId) -> Cards, slotOf: (PlayerId) -> HandSlot, rules: Rules, plr: PlayerId, cards: Cards = handOf(plr)) =
    slotOf(plr).run { handOf(plr).run { cards.forEach { card -> updateStateOf(card) { position(indexOf(card), size, rules.handSize) } } } }

suspend fun updateHandStates(handOf: (PlayerId) -> Cards, slotOf: (PlayerId) -> HandSlot, rules: Rules, plr: PlayerId, cards: Cards = handOf(plr), delay: Long) =
    slotOf(plr).run { handOf(plr).run { cards.forEach { card -> updateStateOf(card, delay) { position(indexOf(card), size, rules.handSize) } } } }

// endregion

fun stateMachine(
    state: State,
    currentState: MutableValue<State>,
    effectFlow: MutableSharedFlow<Effect>? = null,
    action: Action? = null
) = createStateMachine<State, Action, Effect>(state) {
    state<NoState> {
        on<EnterGameAction> { enterGame(it) }
        on<LeaveGameAction> { leaveGame() }
    }
    state<GameState> {
        on<EnterGameAction> { enterGame() }
        on<LeaveGameAction> { leaveGame() }
        on<NewRoundAction> { newRound(it) }
        on<NewTurnAction> { newTurn() }
        on<PutCardAction> { putCard(it) }
        on<SetDoneAction> { setDone(it) }
        on<EndTurnAction> { endTurn(it) }
        on<EndRoundAction> { endRound() }
    }
    onTransition { t ->
        if (t is StateMachine.Transition.Valid) {
            currentState.reduce { t.toState }
            t.sideEffect?.let { sideEffect -> effectFlow?.tryEmit(sideEffect) }
        }
    }
}.apply {
    if (action != null) transition(action)
}

suspend fun State.executeEffect(effect: Effect, slotOf: (PlayerId) -> HandSlot) = when {
    this !is GameState -> null
    effect is EnterGameEffect -> afterEnterGame(effect, slotOf)
    effect is LeaveGameEffect -> afterLeaveGame()
    effect is NewRoundEffect -> afterNewRound(slotOf)
    effect is PutCardEffect -> afterPutCard(effect, slotOf)
    effect is SetDoneEffect -> afterSetDone()
    effect is EndTurnEffect -> afterEndTurn(effect, slotOf)
    effect is EndRoundEffect -> afterEndRound()
    else -> null
}
