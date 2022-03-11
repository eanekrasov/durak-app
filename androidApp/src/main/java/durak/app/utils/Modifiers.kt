package durak.app.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.DefaultCameraDistance
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import durak.app.config.LocalCardScale
import durak.app.game.*

inline val Modifier.w get() = fillMaxWidth()
inline val Modifier.w2 get() = fillMaxWidth(0.5f)
inline val Modifier.s get() = fillMaxSize()
inline val Modifier.z get() = zIndex(1000f)
inline val Modifier.p8 get() = padding(8.dp)
inline val Modifier.p16 get() = padding(16.dp)
inline val Modifier.s16 get() = size(16.dp)
inline val Modifier.s24 get() = size(24.dp)
inline val Modifier.s32 get() = size(32.dp)
inline val Modifier.s36 get() = size(36.dp)
inline val Modifier.s48 get() = size(48.dp)
inline val Modifier.s64 get() = size(64.dp)

@Stable
fun Modifier.movable(enabled: Boolean = true, onDrop: Density.(Offset) -> Unit) = composed {
    var offset by remember { mutableStateOf(Offset.Zero) }
    when {
        enabled -> pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset = Offset.Zero },
                onDragEnd = {
                    onDrop(offset)
                    offset = Offset.Zero
                }
            ) { change, amount ->
                offset += amount
                change.consumeAllChanges()
            }
        }
        else -> this
    }.offset { offset.round() }
}

fun Modifier.slot(it: CardTransition, cardShape: Shape, movingEnabled: Boolean, onDrop: Density.(Offset) -> Unit = {}, onClick: () -> Unit = {}) = zIndex(it.zIndex.value)
    .graphicsLayer {
        translationX = it.offset.value.x.toPx()
        translationY = it.offset.value.y.toPx()
        scaleX = it.scale.value
        scaleY = it.scale.value
        rotationY = if (it.visible.value) it.angleY.value.value else 180f - it.angleY.value.value
        rotationZ = it.angleZ.value.value
        cameraDistance = DefaultCameraDistance
    }
    .movable(movingEnabled, onDrop)
    .graphicsLayer {
        shadowElevation = it.elevation.value
        shape = cardShape
        clip = it.elevation.value > 0
    }
    .clickable(it.visible.value, null, null, onClick)

fun Modifier.slot(
    state: CardState, distance: Float = 1f, angleY: Degree = state.angleY.deg, angleZ: Degree = state.angleZ.deg,
    scale: Float = state.scale, zIndex: Float = state.zIndex, elevation: Float = state.elevation,
    cardShape: Shape? = null
) = composed {
    val screenSize = LocalConfiguration.current.run { DpSize(screenWidthDp.dp, screenHeightDp.dp) }
    val cardScale = LocalCardScale.current
    val offset = (screenSize - DpSize(CardWidthDp.dp, CardWidthDp.dp) * cardScale * scale) * 0.5f * state.bias + state.offset * distance
    slot(offset, angleY, angleZ, scale, cardScale, zIndex, elevation, cardShape ?: MaterialTheme.shapes.small)
}

fun Modifier.slot(
    offset: DpOffset = DpOffset.Zero, angleY: Degree = Degree.Zero, angleZ: Degree = Degree.Zero,
    scale: Float = 1f, cardScale: Float = 1f, zIndex: Float = 0f, elevation: Float = 0f,
    cardShape: Shape, onClick: () -> Unit = {}
) = zIndex(zIndex)
    .graphicsLayer {
        translationX = offset.x.toPx()
        translationY = offset.y.toPx()
        scaleX = scale * cardScale
        scaleY = scale * cardScale
        rotationY = if (angleY < Degree.PI2) angleY.value else 180f - angleY.value
        rotationZ = angleZ.value
        cameraDistance = DefaultCameraDistance
    }
    .graphicsLayer {
        shadowElevation = elevation
        shape = cardShape
        clip = elevation > 0
    }
    .clickable(angleY < Degree.PI2, null, null, onClick)
//.run { if (elevation > 0) shadow(elevation.dp, shape ?: RectangleShape) else this }
