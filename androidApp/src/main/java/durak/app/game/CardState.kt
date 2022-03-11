package durak.app.game

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import durak.app.utils.times
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

typealias Card = Int
typealias Cards = List<Card>

fun Card(rank: Rank, suit: Suit): Card = suit.ordinal * 13 + rank.ordinal
val Card.rank get() = ranks().elementAt(rem(13))
val Card.suit get() = suits().elementAt(div(13))
val Card.label get() = "${suit.symbol}${rank.title}"
fun Cards.sorted(trump: Suit? = null) = sortedBy { it.rank.ordinal + if (it.suit == trump) 13 else 0 }
fun Cards.hand(plr: Int, rules: Rules) = subList(size - (plr + 1) * rules.handSize, size - plr * rules.handSize).sorted(if (rules.trumpless) null else first().suit)
fun beats(card: Card, by: Card, trump: Suit?) = if (by.suit == card.suit) by.rank > card.rank else by.suit == trump
val allCards: Cards = ranks().flatMap { rank -> Suit.values().map { suit -> Card(rank, suit) } }

@Serializable
data class CardState(
    val biasX: Float = 0f, val biasY: Float = 0f,
    val offsetXdp: Float = 0f, val offsetYdp: Float = 0f,
    val angleY: Float = 0f,
    val angleZ: Float = 0f,
    val scale: Float = 1f,
    val zIndex: Float = 0f,
    val elevation: Float = 0f
)

val CardState.bias get() = Offset(biasX, biasY)
val CardState.offset get() = DpOffset(offsetXdp.dp, offsetYdp.dp)

internal fun Offset.offset(screenSize: DpSize, scale: Float) = (screenSize - DpSize(CardWidthDp.dp, CardWidthDp.dp) * scale) * 0.5f * this
internal fun CardState.offset(screenSize: DpSize, cardScale: Float) = bias.offset(screenSize, cardScale * scale) + offset

private val allStates = allCards.associateWith { mutableStateOf(MutableTransitionState(randomSlot.position())) }.toMutableMap()

fun stateOf(card: Card) = allStates.getValue(card)

fun updateStateOf(card: Card, block: (CardState) -> CardState) = stateOf(card).value.run { targetState = block(currentState) }

fun currentStateOf(card: Card) = stateOf(card).value.currentState

fun replaceStateOf(card: Card, block: () -> CardState) = run { stateOf(card).value = MutableTransitionState(block()) }

suspend fun updateStateOf(card: Card, delay: Long, block: (CardState) -> CardState) = updateStateOf(card, block).also { delay(delay) }
