package durak.app.ui

import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.view.MotionEvent
import androidx.annotation.FloatRange
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.hypot
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> CircularReveal(targetState: T, modifier: Modifier = Modifier, animationSpec: FiniteAnimationSpec<Float> = tween(500), content: @Composable CircularRevealScope.(T) -> Unit) {
    val items = remember { mutableStateListOf<CircularRevealAnimationItem<T>>() }
    val transitionState = remember { MutableTransitionState(targetState) }
    val targetChanged = (targetState != transitionState.targetState)
    var offset: Offset? by remember { mutableStateOf(null) }
    transitionState.targetState = targetState
    val transition = updateTransition(transitionState, label = "transition")
    if (targetChanged || items.isEmpty()) {
        // Only manipulate the list when the state is changed, or in the first run.
        val keys = items.map { it.key }.run { if (!contains(targetState)) toMutableList().also { it.add(targetState) } else this }
        items.clear()
        keys.mapIndexedTo(items) { index, key ->
            CircularRevealAnimationItem(key) {
                val progress by transition.animateFloat(transitionSpec = { animationSpec }, label = "") { if (index == keys.size - 1 && it != key) 0f else 1f }
                Box(Modifier.circularReveal(progress, offset)) { with(CircularRevealScope) { content(key) } }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.key != transitionState.targetState }
    }
    Box(modifier.pointerInteropFilter {
        offset = if (it.action == MotionEvent.ACTION_DOWN) Offset(it.x, it.y) else null
        false
    }) { items.forEach { key(it.key) { it.content() } } }
}

@LayoutScopeMarker
@Immutable
object CircularRevealScope

private data class CircularRevealAnimationItem<T>(val key: T, val content: @Composable () -> Unit)

fun Modifier.circularReveal(@FloatRange(from = 0.0, to = 1.0) progress: Float, offset: Offset? = null) = clip(CircularRevealShape(progress, offset))

private class CircularRevealShape(@FloatRange(from = 0.0, to = 1.0) private val progress: Float, private val offset: Offset? = null) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline = Outline.Generic(Path().apply {
        addCircle(offset?.x ?: (size.width / 2f), offset?.y ?: (size.height / 2f), longestDistanceToACorner(size, offset) * progress, CW)
    }.asComposePath())

    private fun longestDistanceToACorner(size: Size, offset: Offset?): Float = when (offset) {
        null -> hypot(size.width / 2f, size.height / 2f)
        else -> hypot(max(offset.x, size.width - offset.x), max(offset.y, size.height - offset.y))
    }
}

