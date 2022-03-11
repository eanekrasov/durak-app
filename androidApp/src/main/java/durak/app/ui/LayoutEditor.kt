package durak.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Link
import androidx.compose.material.icons.twotone.LinkOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import durak.app.config.Layout
import durak.app.config.opponents
import durak.app.game.position
import durak.app.utils.*
import kotlin.random.Random.Default.nextInt

@Composable
fun LayoutEditor(layout: Layout, onValueChange: (Layout) -> Unit) = Column(
    Modifier
        .fillMaxHeight(0.5f)
        .verticalScroll(rememberScrollState())
) {
    // Caption("arrangement: ${layout.arrangement}")
    // VerticalRadioGroup(layout.arrangement, arrayOf(Left, Middle, Right, SpaceAround, SpaceBetween, SpaceEvenly)) { arrangement -> setLayout(layout.copy(arrangement = arrangement)); onFinish() }
    DuSlider(layout.space, { onValueChange(layout.copy(space = it)) }, title = { Text("space: ${layout.space}") }, valueRange = 0f..60f, steps = 11)
    RangeSlider(layout.range, { onValueChange(layout.copy(range = it)) }, title = { Text("range: ${layout.range}") })
    DuSlider(layout.scale, { onValueChange(layout.copy(scale = it)) }, title = { Text("scale: ${layout.scale}") }, valueRange = 0.3f..0.7f, steps = 15)
    DuSlider(layout.sector, { onValueChange(layout.copy(sector = it)) }, title = { Text("sector: ${layout.sector}") }, valueRange = 30f..100f, steps = 13)
    DuSlider(layout.radius, { onValueChange(layout.copy(radius = it)) }, title = { Text("radius: ${layout.radius}") }, valueRange = 0.5f..1.2f, steps = 13)
    DuSlider(layout.distance, { onValueChange(layout.copy(distance = it)) }, title = { Text("distance: ${layout.distance}") }, valueRange = 0f..1.2f, steps = 11)
    DuSlider(layout.elevation, { onValueChange(layout.copy(elevation = it)) }, title = { Text("elevation: ${layout.elevation}") }, valueRange = 0f..10f, steps = 9)
}

@Composable
fun RangeSlider(
    range: ClosedRange<Float>,
    onRangeChange: (ClosedRange<Float>) -> Unit,
    title: @Composable () -> Unit = { Text("range: $range") }
) {
    var linked by remember { mutableStateOf(true) }
    val icon = @Composable { IconButton({ linked = !linked }) { Icon(if (linked) Icons.TwoTone.Link else Icons.TwoTone.LinkOff, null) } }
    when (linked) {
        true -> DuSlider(range.deflate(30f).fold(), { onRangeChange(it.unfold().inflate(30f)) }, Modifier, icon, title, valueRange = 0f..70f)
        else -> DuRangeSlider(range.deflate(30f), { onRangeChange(it.inflate(30f)) }, Modifier, icon, title, valueRange = -70f..70f)
    }
}

private fun ClosedRange<Float>.inflate(value: Float) = (start - value)..(endInclusive + value)
private fun ClosedRange<Float>.deflate(value: Float) = (start + value)..(endInclusive - value)
private fun ClosedRange<Float>.fold() = (endInclusive - start) * 0.5f
private fun Float.unfold() = -this..this

@Composable
fun LayoutPreview(layout: Layout, numPlayers: Int) = Box(Modifier.s, Alignment.Center) {
    val screenRatio = LocalConfiguration.current.run { screenWidthDp.toFloat() / screenHeightDp.toFloat() }
    remember(numPlayers, layout) { layout.opponents(numPlayers, screenRatio) }.forEachIndexed { plr, slot ->
        Text("$plr", Modifier.slot(slot.position(0, 1, 1), angleZ = 0.deg, distance = 0f, scale = 1f, elevation = 0f).p8.z, style = MaterialTheme.typography.caption)
        remember { Array(6) { nextInt(52) }.toList() }.forEachIndexed { idx, card ->
            Card(Modifier.slot(slot.position(idx, 6, 6)), card)
        }
    }
}

@Preview
@Composable
fun LayoutPreviewPreview() = DurakTheme { LayoutPreview(Layout(), 6) }
