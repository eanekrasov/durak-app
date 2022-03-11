package durak.app.config

import durak.app.game.HandSlot
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Serializable
data class Layout(
    val range: ClosedRange<Float> = -90f..90f,
    val space: Float = 0f,
    val sector: Float = 90f,
    val scale: Float = 0.4f,
    val distance: Float = 1f,
    val radius: Float = 1f,
    val elevation: Float = 0f
)

fun Layout.opponents(numPlayers: Int, ratio: Float) = Array(numPlayers) { 0f }.also { outPositions ->
    val spaces = Array(numPlayers - 1) { space }
    val gapSize = if (spaces.isNotEmpty()) (range.endInclusive - range.start - spaces.fold(0f) { a, b -> a + b }) / spaces.size else 0f
    var current = range.start
    spaces.forEachIndexed { index, space ->
        outPositions[index] = current
        current += space + gapSize
    }
    outPositions[spaces.size] = current
}.map { angle -> HandSlot(radius * sin(angle * PI.toFloat() / 180f), -0.5f + (radius - ratio) * -cos(angle * PI.toFloat() / 180f), false, angle - 180f, sector, scale, distance, elevation) }.toSet()
