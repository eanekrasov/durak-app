package durak.app.game

import durak.app.utils.lerp
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random.Default.nextFloat

fun interface PileSlot {
    fun position(idx: Int): CardState
}

fun interface RandomSlot {
    fun position(): CardState
}

fun interface TalonSlot {
    fun position(idx: Int, trumpless: Boolean): CardState
}

@Serializable
data class HandSlot(
    val biasX: Float = 0f,
    val biasY: Float = 0f,
    val faced: Boolean = false,
    val direction: Float = 0f,
    val sector: Float = 90f,
    val scale: Float = 0.4f,
    val distance: Float = 1f,
    val elevation: Float = 0f
)

fun HandSlot.angleOf(idx: Int, count: Int, max: Int) = if (count > 1) sector * min(count, max) * (idx / (count - 1f) - 0.5f) / max else 0f

fun HandSlot.position(idx: Int, count: Int, max: Int) = (direction + angleOf(idx, count, max)).let { angleZ ->
    val d = CardHeightDp * scale * distance
    val zRad = angleZ * PI.toFloat() / 180f
    CardState(biasX, biasY, d * sin(zRad), d * -cos(zRad), if (faced) 0f else 180f, angleZ, scale, idx.toFloat(), elevation)
}

val pileSlot = PileSlot { idx ->
    CardState(lerp(0.95f, 1.02f, nextFloat()), lerp(-0.2f, 0.4f, nextFloat()), 0f, 0f, 180f, lerp(-250f, 250f, nextFloat()), 0.5f, idx + 0f, 0f)
}

val randomSlot = RandomSlot {
    CardState(lerp(-1f, 1f, nextFloat()), lerp(-1f, 1f, nextFloat()), 0f, 0f, 0f, lerp(-250f, 250f, nextFloat()), 1f, nextFloat() * 100f, 0f)
}

val talonSlot = TalonSlot { idx, trumpless ->
    CardState(if (idx == 0 && !trumpless) -0.86f else -1f, 0f, 0f, 0f, if (idx == 0 && !trumpless) 0f else 180f, if (idx == 0 && !trumpless) 90f else 180f, 0.5f, idx + 0f, 0f)
}

val playerSlotDefault = HandSlot(0f, 1.6f, true, 0f, 65f, 1.5f, 1.5f, 20f)
val playerSlotCurved = HandSlot(0f, 1.4f, true, 0f, 90f, 1.5f, 1f, 20f)
val playerSlotBig = HandSlot(0f, 4.9f, true, 0f, 11f, 1.5f, 9.3f, 20f)
val playerSlots = mapOf(playerSlotDefault to "Default", playerSlotCurved to "Curved", playerSlotBig to "Big")
