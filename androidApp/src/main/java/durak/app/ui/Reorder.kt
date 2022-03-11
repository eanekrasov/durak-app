@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import durak.app.utils.fastAll
import durak.app.utils.fastAny
import durak.app.utils.fastFirstOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

@Composable
fun rememberReorderState(
    listState: LazyListState = rememberLazyListState(),
) = remember { ReorderableState(listState) }

class ReorderableState(val listState: LazyListState) {
    var draggedIndex by mutableStateOf<Int?>(null)
        internal set

    internal val ch = Channel<StartDrag>()

    @Suppress("MemberVisibilityCanBePrivate")
    val draggedKey by derivedStateOf { selected?.key }

    @Suppress("MemberVisibilityCanBePrivate")
    val draggedOffset by derivedStateOf {
        draggedIndex
            ?.let { listState.layoutInfo.itemInfoByIndex(it) }
            ?.let { (selected?.offset?.toFloat() ?: 0f) + movedDist - it.offset }
    }

    fun offsetByKey(key: Any) =
        if (draggedKey == key) draggedOffset else null

    fun offsetByIndex(index: Int) =
        if (draggedIndex == index) draggedOffset else null

    internal var selected by mutableStateOf<LazyListItemInfo?>(null)
    internal var movedDist by mutableStateOf(0f)
}

fun Modifier.reorderable(
    state: ReorderableState,
    onMove: (fromPos: ItemPosition, toPos: ItemPosition) -> (Unit),
    canDragOver: ((index: ItemPosition) -> Boolean)? = null,
    onDragEnd: ((startIndex: Int, endIndex: Int) -> (Unit))? = null,
    orientation: Orientation = Orientation.Vertical,
    maxScrollPerFrame: Dp = 20.dp,
) = composed {
    val job: MutableState<Job?> = remember { mutableStateOf(null) }
    val maxScroll = with(LocalDensity.current) { maxScrollPerFrame.toPx() }
    val logic = remember { ReorderLogic(state, onMove, canDragOver, onDragEnd) }
    val scope = rememberCoroutineScope()
    val interactions = remember { MutableSharedFlow<ReorderAction>(extraBufferCapacity = 16) }
    fun cancelAutoScroll() {
        job.value = job.value?.let {
            it.cancel()
            null
        }
    }
    LaunchedEffect(state) {
        merge(
            interactions,
            snapshotFlow { state.listState.layoutInfo }
                .distinctUntilChanged { old, new ->
                    old.visibleItemsInfo.firstOrNull()?.key == new.visibleItemsInfo.firstOrNull()?.key && old.visibleItemsInfo.lastOrNull()?.key == new.visibleItemsInfo.lastOrNull()?.key
                }.map { ReorderAction.Drag(0f) }
        )
            .collect { event ->
                when (event) {
                    is ReorderAction.End -> {
                        cancelAutoScroll()
                        logic.endDrag()
                    }
                    is ReorderAction.Start -> {
                        logic.startDrag(event.key)
                    }
                    is ReorderAction.Drag -> {
                        if (logic.dragBy(event.amount) && job.value?.isActive != true) {
                            val scrollOffset = logic.calcAutoScrollOffset(0, maxScroll)
                            if (scrollOffset != 0f) {
                                job.value =
                                    scope.launch {
                                        var scroll = scrollOffset
                                        var start = 0L
                                        while (scroll != 0f && job.value?.isActive == true) {
                                            withFrameMillis {
                                                if (start == 0L) {
                                                    start = it
                                                } else {
                                                    scroll = logic.calcAutoScrollOffset(it - start, maxScroll)
                                                }
                                            }
                                            if (logic.scrollBy(scroll) != scroll) {
                                                scroll = 0f
                                            }
                                        }
                                    }
                            } else {
                                cancelAutoScroll()
                            }
                        }
                    }
                }
            }
    }

    Modifier.pointerInput(Unit) {
        forEachGesture {
            val dragStart = state.ch.receive()
            val down = awaitPointerEventScope {
                currentEvent.changes.fastFirstOrNull { it.id == dragStart.id }
            }
            val item = down?.position?.let { position ->
                val off = state.listState.layoutInfo.viewportStartOffset + position.forOrientation(orientation).toInt()
                state.listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { off in it.offset..(it.offset + it.size) }
            }
            if (down != null && item != null) {
                interactions.tryEmit(ReorderAction.Start(item.key))
                dragStart.offet?.also {
                    interactions.tryEmit(ReorderAction.Drag(it.forOrientation(orientation)))
                }
                detectDrag(
                    down.id,
                    onDragEnd = { interactions.tryEmit(ReorderAction.End) },
                    onDragCancel = { interactions.tryEmit(ReorderAction.End) },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        interactions.tryEmit(ReorderAction.Drag(dragAmount.forOrientation(orientation)))
                    })
            }
        }
    }
}

private suspend fun PointerInputScope.detectDrag(
    down: PointerId,
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) {
    awaitPointerEventScope {
        if (
            drag(down) {
                onDrag(it, it.positionChange())
                it.consumePositionChange()
            }
        ) {
            // consume up if we quit drag gracefully with the up
            currentEvent.changes.forEach {
                if (it.changedToUp()) {
                    it.consumeDownChange()
                }
            }
            onDragEnd()
        } else {
            onDragCancel()
        }
    }
}

private fun Offset.forOrientation(orientation: Orientation) = if (orientation == Orientation.Vertical) y else x

private sealed class ReorderAction {
    class Start(val key: Any) : ReorderAction()
    class Drag(val amount: Float) : ReorderAction()
    object End : ReorderAction()
}

internal data class StartDrag(val id: PointerId, val offet: Offset? = null)

internal class ReorderLogic(
    private val state: ReorderableState,
    private val onMove: (fromIndex: ItemPosition, toIndex: ItemPosition) -> (Unit),
    private val canDragOver: ((index: ItemPosition) -> Boolean)? = null,
    private val onDragEnd: ((startIndex: Int, endIndex: Int) -> (Unit))? = null,
) {
    fun startDrag(key: Any) =
        state.listState.layoutInfo.visibleItemsInfo
            .fastFirstOrNull { it.key == key }
            ?.also { info ->
                state.selected = info
                state.draggedIndex = info.index
            }

    suspend fun dragBy(amount: Float): Boolean =
        state.draggedIndex?.let {
            state.movedDist += amount
            checkIfMoved()
            true
        } ?: false

    fun endDrag() {
        val startIndex = state.selected?.index
        val endIndex = state.draggedIndex
        state.draggedIndex = null
        state.selected = null
        state.movedDist = 0f
        onDragEnd?.apply {
            if (startIndex != null && endIndex != null) {
                invoke(startIndex, endIndex)
            }
        }
    }

    suspend fun scrollBy(value: Float) = state.listState.scrollBy(value)

    fun calcAutoScrollOffset(time: Long, maxScroll: Float): Float =
        state.selected?.let { selected ->
            val start = (state.movedDist + selected.offset)
            when {
                state.movedDist < 0 -> (start - viewportStartOffset).takeIf { it < 0 }
                state.movedDist > 0 -> (start + selected.size - viewportEndOffset).takeIf { it > 0 }
                else -> null
            }
                ?.takeIf { it != 0f }
                ?.let { interpolateOutOfBoundsScroll(selected.size, it, time, maxScroll) }
        } ?: 0f

    private suspend fun checkIfMoved() {
        state.selected?.also { selected ->
            val start = (state.movedDist + selected.offset)
                .coerceIn(viewportStartOffset - selected.size, viewportEndOffset)
            val end = (start + selected.size)
                .coerceIn(viewportStartOffset, viewportEndOffset + selected.size)
            state.draggedIndex?.also { draggedItem ->
                chooseDropItem(
                    state.listState.layoutInfo.visibleItemsInfo
                        .filterNot { it.offsetEnd() < start || it.offset > end || it.index == draggedItem }
                        .filter { canDragOver?.invoke(ItemPosition(it.index, it.key)) != false },
                    start,
                    end
                )?.also { targetIdx ->
                    onMove(ItemPosition(draggedItem, selected.key), ItemPosition(targetIdx.index, targetIdx.key))
                    state.draggedIndex = targetIdx.index
                    state.listState.scrollToItem(state.listState.firstVisibleItemIndex, state.listState.firstVisibleItemScrollOffset)
                }
            }
        }
    }

    private fun chooseDropItem(
        items: List<LazyListItemInfo>,
        curStart: Float,
        curEnd: Float,
    ): LazyListItemInfo? =
        draggedItem.let { draggedItem ->
            var targetItem: LazyListItemInfo? = null
            if (draggedItem != null) {
                val distance = curStart - draggedItem.offset
                if (distance != 0f) {
                    var targetDiff = -1f
                    for (index in items.indices) {
                        val item = items[index]
                        (when {
                            distance > 0 -> (item.offsetEnd() - curEnd)
                                .takeIf { diff -> diff < 0 && item.offsetEnd() > draggedItem.offsetEnd() }
                            else -> (item.offset - curStart)
                                .takeIf { diff -> diff > 0 && item.offset < draggedItem.offset }
                        })
                            ?.absoluteValue
                            ?.takeIf { it > targetDiff }
                            ?.also {
                                targetDiff = it
                                targetItem = item
                            }
                    }
                }
            } else if (state.draggedIndex != null) {
                targetItem = items.lastOrNull()
            }
            targetItem
        }


    private fun LazyListItemInfo.offsetEnd() =
        offset + size

    private val draggedItem get() = state.draggedIndex?.let { state.listState.layoutInfo.itemInfoByIndex(it) }
    private val viewportStartOffset get() = state.listState.layoutInfo.viewportStartOffset.toFloat()
    private val viewportEndOffset get() = state.listState.layoutInfo.viewportEndOffset.toFloat()

    companion object {
        private const val ACCELERATION_LIMIT_TIME_MS: Long = 1500
        private val EaseOutQuadInterpolator: (Float) -> (Float) = {
            val t = 1 - it
            1 - t * t * t * t
        }
        private val EaseInQuintInterpolator: (Float) -> (Float) = {
            it * it * it * it * it
        }

        fun interpolateOutOfBoundsScroll(
            viewSize: Int,
            viewSizeOutOfBounds: Float,
            time: Long,
            maxScroll: Float,
        ): Float {
            val outOfBoundsRatio = min(1f, 1f * viewSizeOutOfBounds.absoluteValue / viewSize)
            val cappedScroll =
                sign(viewSizeOutOfBounds) * maxScroll * EaseOutQuadInterpolator(outOfBoundsRatio)
            val timeRatio =
                if (time > ACCELERATION_LIMIT_TIME_MS) 1f else time.toFloat() / ACCELERATION_LIMIT_TIME_MS
            return (cappedScroll * EaseInQuintInterpolator(timeRatio)).let {
                if (it == 0f) {
                    if (viewSizeOutOfBounds > 0) 1f else -1f
                } else {
                    it
                }
            }
        }
    }
}

internal fun LazyListLayoutInfo.itemInfoByIndex(index: Int) =
    visibleItemsInfo.getOrNull(index - visibleItemsInfo.first().index)

fun <T> MutableList<T>.move(fromIdx: Int, toIdx: Int) {
    when {
        fromIdx == toIdx -> {
            return
        }
        toIdx > fromIdx -> {
            for (i in fromIdx until toIdx) {
                this[i] = this[i + 1].also { this[i + 1] = this[i] }
            }
        }
        else -> {
            for (i in fromIdx downTo toIdx + 1) {
                this[i] = this[i - 1].also { this[i - 1] = this[i] }
            }
        }
    }
}

data class ItemPosition(val index: Int, val key: Any)

fun Modifier.draggedItem(
    offset: Float?,
    orientation: Orientation = Orientation.Vertical,
): Modifier = composed {
    Modifier
        .zIndex(offset?.let { 1f } ?: 0f)
        .graphicsLayer {
            with(offset ?: 0f) {
                if (orientation == Orientation.Vertical) {
                    translationY = this
                } else {
                    translationX = this
                }
            }
            shadowElevation = offset?.let { 8f } ?: 0f
        }
}

// Copied from DragGestureDetector , as long the pointer api isn`t ready.

internal suspend fun AwaitPointerEventScope.awaitPointerSlopOrCancellation(
    pointerId: PointerId,
    pointerType: PointerType,
    onPointerSlopReached: (change: PointerInputChange, overSlop: Offset) -> Unit,
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    var offset = Offset.Zero
    val touchSlop = viewConfiguration.pointerSlop(pointerType)
    var pointer = pointerId
    while (true) {
        val event = awaitPointerEvent()
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer }!!
        if (dragEvent.positionChangeConsumed()) {
            return null
        } else if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return null
            } else {
                pointer = otherDown.id
            }
        } else {
            offset += dragEvent.positionChange()
            val distance = offset.getDistance()
            var acceptedDrag = false
            if (distance >= touchSlop) {
                val touchSlopOffset = offset / distance * touchSlop
                onPointerSlopReached(dragEvent, offset - touchSlopOffset)
                if (dragEvent.positionChangeConsumed()) {
                    acceptedDrag = true
                } else {
                    offset = Offset.Zero
                }
            }

            if (acceptedDrag) {
                return dragEvent
            } else {
                awaitPointerEvent(PointerEventPass.Final)
                if (dragEvent.positionChangeConsumed()) {
                    return null
                }
            }
        }
    }
}

internal suspend fun PointerInputScope.awaitLongPressOrCancellation(
    initialDown: PointerInputChange,
): PointerInputChange? {
    var longPress: PointerInputChange? = null
    var currentDown = initialDown
    val longPressTimeout = viewConfiguration.longPressTimeoutMillis
    return try {
        // wait for first tap up or long press
        withTimeout(longPressTimeout) {
            awaitPointerEventScope {
                var finished = false
                while (!finished) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.changes.fastAll { it.changedToUpIgnoreConsumed() }) {
                        // All pointers are up
                        finished = true
                    }


                    if (event.changes.fastAny { it.consumed.downChange || it.isOutOfBounds(size) }) {
                        finished = true // Canceled
                    }

                    // Check for cancel by position consumption. We can look on the Final pass of
                    // the existing pointer event because it comes after the Main pass we checked
                    // above.
                    val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                    if (consumeCheck.changes.fastAny { it.positionChangeConsumed() }) {
                        finished = true
                    }
                    if (!event.isPointerUp(currentDown.id)) {
                        longPress = event.changes.fastFirstOrNull { it.id == currentDown.id }
                    } else {
                        val newPressed = event.changes.fastFirstOrNull { it.pressed }
                        if (newPressed != null) {
                            currentDown = newPressed
                            longPress = currentDown
                        } else {
                            // should technically never happen as we checked it above
                            finished = true
                        }
                    }
                }
            }
        }
        null
    } catch (_: TimeoutCancellationException) {
        longPress ?: initialDown
    }
}

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true

// This value was determined using experiments and common sense.
// We can't use zero slop, because some hypothetical desktop/mobile devices can send
// pointer events with a very high precision (but I haven't encountered any that send
// events with less than 1px precision)
private val mouseSlop = 0.125.dp
private val defaultTouchSlop = 18.dp // The default touch slop on Android devices
private val mouseToTouchSlopRatio = mouseSlop / defaultTouchSlop

// TODO(demin): consider this as part of ViewConfiguration class after we make *PointerSlop*
//  functions public (see the comment at the top of the file).
//  After it will be a public API, we should get rid of `touchSlop / 144` and return absolute
//  value 0.125.dp.toPx(). It is not possible right now, because we can't access density.
private fun ViewConfiguration.pointerSlop(pointerType: PointerType): Float {
    return when (pointerType) {
        PointerType.Mouse -> touchSlop * mouseToTouchSlopRatio
        else -> touchSlop
    }
}

fun Modifier.detectReorder(state: ReorderableState) =
    this.then(
        Modifier.pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var drag: PointerInputChange?
                    var overSlop = Offset.Zero
                    do {
                        drag = awaitPointerSlopOrCancellation(down.id, down.type) { change, over ->
                            change.consumePositionChange()
                            overSlop = over
                        }
                    } while (drag != null && !drag.positionChangeConsumed())
                    if (drag != null) {
                        state.ch.trySend(StartDrag(down.id, overSlop))
                    }
                }
            }
        }
    )

fun Modifier.detectReorderAfterLongPress(state: ReorderableState) = pointerInput(Unit) {
    forEachGesture {
        val down = awaitPointerEventScope {
            awaitFirstDown(requireUnconsumed = false)
        }
        awaitLongPressOrCancellation(down)?.also {
            state.ch.trySend(StartDrag(down.id))
        }
    }
}
