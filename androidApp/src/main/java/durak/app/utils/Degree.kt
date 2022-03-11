@file:Suppress("NOTHING_TO_INLINE", "INLINE_CLASS_DEPRECATED")

package durak.app.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.math.min

object DegreeSerializer : KSerializer<Degree> {
    private val float = serializer<Float>()
    override val descriptor = float.descriptor
    override fun deserialize(decoder: Decoder) = float.deserialize(decoder).deg
    override fun serialize(encoder: Encoder, value: Degree) = float.serialize(encoder, value.value)
}

@Serializable(DegreeSerializer::class)
@Immutable
inline class Degree(val value: Float) : Comparable<Degree> {
    @Stable
    inline operator fun plus(other: Degree) = (value + other.value).deg
    @Stable
    inline operator fun minus(other: Degree) = (value - other.value).deg
    @Stable
    inline operator fun unaryMinus() = (-value).deg
    @Stable
    inline operator fun div(other: Float) = (value / other).deg
    @Stable
    inline operator fun div(other: Int) = (value / other).deg
    @Stable
    inline operator fun div(other: Degree) = value / other.value
    @Stable
    inline operator fun times(other: Float) = (value * other).deg
    @Stable
    inline operator fun times(other: Int) = (value * other).deg
    @Stable
    override operator fun compareTo(other: Degree) = value.compareTo(other.value)
    @Stable
    override fun toString() = "$value.deg"

    companion object {
        val Zero = Degree(0f)
        val PI = Degree(180f)
        val PI2 = Degree(90f)
        val PI4 = Degree(45f)
    }
}

@Stable
inline val Int.deg
    get() = if (this == 0) Degree.Zero else Degree(toFloat())
@Stable
inline val Float.deg
    get() = if (this == 0f) Degree.Zero else Degree(this)

@Stable
inline operator fun Int.times(other: Degree) = times(other.value).deg
@Stable
inline operator fun Float.times(other: Degree) = times(other.value).deg
@Stable
inline fun min(a: Degree, b: Degree) = min(a.value, b.value).deg

val DegreeToVector = TwoWayConverter<Degree, AnimationVector1D>({ AnimationVector1D(it.value) }, { it.value.deg })

@Composable
fun <S> Transition<S>.animateDegree(
    transitionSpec: @Composable Transition.Segment<S>.() -> FiniteAnimationSpec<Degree> = { spring(visibilityThreshold = Degree.Zero) },
    label: String = "DegreeAnimation",
    targetValueByState: @Composable (state: S) -> Degree
): State<Degree> = animateValue(DegreeToVector, transitionSpec, label, targetValueByState)
