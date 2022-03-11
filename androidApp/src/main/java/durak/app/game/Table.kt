package durak.app.game

import durak.app.utils.lerp
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextFloat

@Serializable
data class Table(
    private val screenWidthDp: Float,
    private val screenHeightDp: Float,
    private val cardScale: Float,
    val states: MutableMap<Int, CardState> = newTableStates(screenWidthDp, screenHeightDp),
    val cards: Map<Card, Card?> = mapOf(),
) {
    val openCards get() = cards.mapNotNull { (a, b) -> a.takeIf { b == null } }
    val allCards get() = cards.flatMap { (a, b) -> listOfNotNull(a, b) }

    operator fun get(idx: Int) = states.getOrPut(idx) {
        val r2 = (randomRadius * cardScale).let { it * it }
        val point = states.values.map { it.offset(screenWidthDp, screenHeightDp, cardScale) }.run {
            flatMap { (ax, ay) ->
                flatMap { (bx, by) ->
                    if (ax == bx && ay == by) listOf() else {
                        val d2 = distance2(ax to ay, bx to by)
                        when {
                            r2 * 4f < d2 -> listOf()
                            r2 * 4f > d2 -> {
                                val (cx, cy) = (ax + bx) * 0.5f to (ay + by) * 0.5f
                                val (dx, dy) = sqrt(r2 - d2 * 0.25f) * (ay - by) / sqrt(d2) to sqrt(r2 - d2 * 0.25f) * (bx - ax) / sqrt(d2)
                                listOf(cx + dx to cy + dy, cx - dx to cy - dy)
                            }
                            else -> listOf((ax + bx) * 0.5f to (ay + by) * 0.5f)
                        }
                    }
                }
            }.toSet().filter { none { p -> it != p && distance2(it, p) < r2 * 0.9f } }.minByOrNull { distance2(0f to 0f, it) }
        }
        tableStateOf(point ?: 0f to 0f, states.size.toFloat())
    }
    fun withCards(card1: Card, card2: Card?) = copy(cards = cards.toMutableMap().apply { set(card1, card2) })
    fun empty() = copy(cards = mapOf(), states = newTableStates(screenWidthDp, screenHeightDp))
    fun closestCard(card: Card) = openCards.closestTo(card, screenWidthDp, screenHeightDp, cardScale)
    companion object {
        fun initial(screenWidthDp: Float, screenHeightDp: Float, cardScale: Float) = Table(screenWidthDp, screenHeightDp, cardScale, newTableStates(screenWidthDp, screenHeightDp), mapOf())
        private val randomRadius get() = (1f + nextFloat() * 0.3f) * CardHeightDp
        private val randomAngle get() = PI.toFloat() * 2f * nextFloat()
        private fun newTableStates(screenWidthDp: Float, screenHeightDp: Float, r: Float = randomRadius, a: Float = randomAngle) = mutableMapOf(
            0 to tableStateOf(0f to 0f, 0f),
            1 to tableStateOf(r * cos(a) to r * sin(a) * screenWidthDp / screenHeightDp, 1f)
        )
        private fun CardState.offset(screenWidthDp: Float, screenHeightDp: Float, cardScale: Float) = (screenWidthDp - CardWidthDp * cardScale * scale) * 0.5f * biasX + offsetXdp to (screenHeightDp - CardWidthDp * cardScale * scale) * 0.5f * biasY + offsetYdp
        private fun distance2(a: Pair<Float, Float>, b: Pair<Float, Float>) = (a.first - b.first).let { it * it } + (a.second - b.second).let { it * it }
        private fun tableStateOf(xy: Pair<Float, Float>, zIndex: Float) = CardState(0f, 0f, xy.first, xy.second, 0f, lerp(5f, 65f, nextFloat()), 1f, zIndex, 0f)
        private fun Cards.closestTo(card: Card, w: Float, h: Float, scale: Float) = associateWith { sqrt(distance2(currentStateOf(card).offset(w, h, scale), currentStateOf(it).offset(w, h, scale))) }.minByOrNull { it.value }
    }
}

