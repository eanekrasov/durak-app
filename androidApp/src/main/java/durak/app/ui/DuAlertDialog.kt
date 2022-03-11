package durak.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import durak.app.utils.fastForEachIndexed
import kotlin.math.max

@Composable
fun DuAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
) = Dialog(onDismissRequest, DialogProperties()) {
    Surface(shape = shapes.medium) {
        Column {
            Layout(
                {
                    title?.let { title ->
                        Box(TitlePadding.layoutId("title").align(Alignment.Start)) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                                ProvideTextStyle(typography.subtitle1, title)
                            }
                        }
                    }
                    text?.let { text ->
                        Box(TextPadding.layoutId("text").align(Alignment.Start)) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                ProvideTextStyle(typography.body2, text)
                            }
                        }
                    }
                },
                Modifier.weight(1f, false),
            ) { measurables, constraints ->
                // Measure with loose constraints for height as we don't want the text to take up more space than it needs
                val titlePlaceable = measurables.firstOrNull { it.layoutId == "title" }?.measure(constraints.copy(minHeight = 0))
                val textPlaceable = measurables.firstOrNull { it.layoutId == "text" }?.measure(constraints.copy(minHeight = 0))
                val layoutWidth = max(titlePlaceable?.width ?: 0, textPlaceable?.width ?: 0)
                val firstTitleBaseline = titlePlaceable?.get(FirstBaseline)?.let { baseline -> if (baseline == AlignmentLine.Unspecified) null else baseline } ?: 0
                val lastTitleBaseline = titlePlaceable?.get(LastBaseline)?.let { baseline -> if (baseline == AlignmentLine.Unspecified) null else baseline } ?: 0
                val titleOffset = TitleBaselineDistanceFromTop.roundToPx()
                // Place the title so that its first baseline is titleOffset from the top
                val titlePositionY = titleOffset - firstTitleBaseline
                val firstTextBaseline = textPlaceable?.get(FirstBaseline)?.let { baseline -> if (baseline == AlignmentLine.Unspecified) null else baseline } ?: 0
                val textOffset = if (titlePlaceable == null) TextBaselineDistanceFromTop.roundToPx() else TextBaselineDistanceFromTitle.roundToPx()
                // Combined height of title and spacing above
                val titleHeightWithSpacing = titlePlaceable?.let { it.height + titlePositionY } ?: 0
                // Align the bottom baseline of the text with the bottom baseline of the title, and then add the offset
                val textPositionY = if (titlePlaceable == null) {
                    // If there is no title, just place the text offset from the top of the dialog
                    textOffset - firstTextBaseline
                } else {
                    if (lastTitleBaseline == 0) {
                        // If `title` has no baseline, just place the text's baseline textOffset from the bottom of the title
                        titleHeightWithSpacing - firstTextBaseline + textOffset
                    } else {
                        // Otherwise place the text's baseline textOffset from the title's last baseline
                        (titlePositionY + lastTitleBaseline) - firstTextBaseline + textOffset
                    }
                }
                // Combined height of text and spacing above
                val textHeightWithSpacing = textPlaceable?.let {
                    if (lastTitleBaseline == 0) textPlaceable.height + textOffset - firstTextBaseline else textPlaceable.height + textOffset - firstTextBaseline - ((titlePlaceable?.height ?: 0) - lastTitleBaseline)
                } ?: 0
                val layoutHeight = titleHeightWithSpacing + textHeightWithSpacing
                layout(layoutWidth, layoutHeight) {
                    titlePlaceable?.place(0, titlePositionY)
                    textPlaceable?.place(0, textPositionY)
                }
            }
            Box(Modifier.fillMaxWidth().padding(8.dp, 2.dp)) {
                Layout({
                    dismissButton?.invoke()
                    confirmButton()
                }) { measurables, constraints ->
                    val sequences = mutableListOf<List<Placeable>>()
                    val crossAxisSizes = mutableListOf<Int>()
                    val crossAxisPositions = mutableListOf<Int>()
                    var mainAxisSpace = 0
                    var crossAxisSpace = 0
                    val currentSequence = mutableListOf<Placeable>()
                    var currentMainAxisSize = 0
                    var currentCrossAxisSize = 0
                    val childConstraints = Constraints(maxWidth = constraints.maxWidth)

                    // Return whether the placeable can be added to the current sequence.
                    fun canAddToCurrentSequence(placeable: Placeable) = currentSequence.isEmpty() || currentMainAxisSize + 8.dp.roundToPx() + placeable.width <= constraints.maxWidth

                    // Store current sequence information and start a new sequence.
                    fun startNewSequence() {
                        if (sequences.isNotEmpty()) crossAxisSpace += 12.dp.roundToPx()
                        sequences += currentSequence.toList()
                        crossAxisSizes += currentCrossAxisSize
                        crossAxisPositions += crossAxisSpace
                        crossAxisSpace += currentCrossAxisSize
                        mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)
                        currentSequence.clear()
                        currentMainAxisSize = 0
                        currentCrossAxisSize = 0
                    }
                    for (measurable in measurables) {
                        // Ask the child for its preferred size.
                        val placeable = measurable.measure(childConstraints)
                        // Start a new sequence if there is not enough space.
                        if (!canAddToCurrentSequence(placeable)) startNewSequence()
                        // Add the child to the current sequence.
                        if (currentSequence.isNotEmpty()) currentMainAxisSize += 8.dp.roundToPx()
                        currentSequence.add(placeable)
                        currentMainAxisSize += placeable.width
                        currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
                    }
                    if (currentSequence.isNotEmpty()) startNewSequence()
                    val mainAxisLayoutSize = if (constraints.maxWidth != Constraints.Infinity) {
                        constraints.maxWidth
                    } else {
                        max(mainAxisSpace, constraints.minWidth)
                    }
                    val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)
                    layout(mainAxisLayoutSize, crossAxisLayoutSize) {
                        sequences.fastForEachIndexed { i, placeables ->
                            val childrenMainAxisSizes = IntArray(placeables.size) { j -> placeables[j].width + if (j < placeables.lastIndex) 8.dp.roundToPx() else 0 }
                            val arrangement = Arrangement.Bottom
                            val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                            with(arrangement) {
                                arrange(mainAxisLayoutSize, childrenMainAxisSizes, mainAxisPositions)
                            }
                            placeables.fastForEachIndexed { j, placeable -> placeable.place(x = mainAxisPositions[j], y = crossAxisPositions[i]) }
                        }
                    }
                }
            }
        }
    }
}

private val TitlePadding = Modifier.padding(start = 24.dp, end = 24.dp)
private val TextPadding = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 28.dp)
private val TitleBaselineDistanceFromTop = 40.sp
private val TextBaselineDistanceFromTitle = 36.sp
private val TextBaselineDistanceFromTop = 38.sp
