@file:Suppress("unused")

package durak.app.ui

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RangeSlider
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DuSlider(
    state: DuValueState<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    sliderModifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) = DuSlider(state.value, { state.value = it; onValueChange(state.value) }, modifier, icon, title, sliderModifier, enabled, valueRange, steps, onValueChangeFinished)

@Composable
fun DuIntSlider(
    state: DuValueState<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    sliderModifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: kotlin.ranges.IntRange = 0..Int.MAX_VALUE,
    onValueChangeFinished: (() -> Unit)? = null,
) = DuIntSlider(state.value, { state.value = it; onValueChange(state.value) }, modifier, icon, title, sliderModifier, enabled, valueRange, onValueChangeFinished)

@Composable
fun DuRangeSlider(
    state: DuValueState<ClosedFloatingPointRange<Float>>, // = rememberRangeDuState(),
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    sliderModifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) = DuRangeSlider(state.value, { state.value = it; onValueChange(state.value) }, modifier, icon, title, sliderModifier, enabled, valueRange, steps, onValueChangeFinished)

@Composable
fun DuSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    sliderModifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) = TileItem(modifier, icon, title, {
    Slider(value, onValueChange, Modifier.padding(end = 16.dp).then(sliderModifier), enabled, valueRange, steps, onValueChangeFinished)
})

@Composable
fun DuIntSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    sliderModifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: kotlin.ranges.IntRange = 0..Int.MAX_VALUE,
    onValueChangeFinished: (() -> Unit)? = null,
) = TileItem(modifier, icon, title, {
    Slider(value.toFloat(), { onValueChange(it.roundToInt()) }, Modifier.padding(end = 16.dp).then(sliderModifier), enabled, valueRange.toFloat(), valueRange.steps, onValueChangeFinished)
})

fun kotlin.ranges.IntRange.toFloat() = first.toFloat()..last.toFloat()
val kotlin.ranges.IntRange.steps get() = last - first - 1

@Composable
fun DuRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    sliderModifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) = TileItem(modifier, icon, title, {
    RangeSlider(value, onValueChange, Modifier.padding(end = 16.dp).then(sliderModifier), enabled, valueRange, steps, onValueChangeFinished)
})
