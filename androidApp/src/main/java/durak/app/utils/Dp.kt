@file:Suppress("NOTHING_TO_INLINE")

package durak.app.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.*

val DpOffsetToVector = TwoWayConverter<DpOffset, AnimationVector2D>({ AnimationVector2D(it.x.value, it.y.value) }, { DpOffset(it.v1.dp, it.v2.dp) })

@Composable
inline fun <S> Transition<S>.animateDpOffset(
    noinline transitionSpec: @Composable (Transition.Segment<S>.() -> FiniteAnimationSpec<DpOffset>) = { spring(Spring.DampingRatioNoBouncy, Spring.StiffnessVeryLow) },
    label: String = "DpOffsetAnimation",
    targetValueByState: @Composable (state: S) -> DpOffset
): State<DpOffset> = animateValue(DpOffsetToVector, transitionSpec, label, targetValueByState)

@Stable
inline operator fun DpOffset.times(other: Float) = DpOffset(x * other, y * other)

@Stable
inline operator fun Offset.times(other: Dp) = DpOffset(other * x, other * y)

@Stable
inline operator fun DpSize.times(other: Offset) = DpOffset(width * other.x, height * other.y)

@Stable
inline operator fun DpSize.times(other: Size) = DpSize(width * other.width, height * other.height)

@Stable
inline operator fun DpSize.times(other: Rect) = DpRect(width * other.left, height * other.top, width * other.right, height * other.bottom)
