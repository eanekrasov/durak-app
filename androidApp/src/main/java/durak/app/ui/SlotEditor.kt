package durak.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import durak.app.game.HandSlot
import durak.app.game.playerSlots
import durak.app.game.position
import durak.app.utils.p8
import durak.app.utils.s
import durak.app.utils.slot
import kotlin.random.Random.Default.nextInt

@Composable
fun SlotEditor(slot: HandSlot, onValueChange: (HandSlot) -> Unit) = Column(Modifier.p8.verticalScroll(rememberScrollState())) {
    DuList(playerSlots.keys.indexOf(slot), { onValueChange(playerSlots.keys.elementAt(it)) }, items = playerSlots.values.toList(), title = { Text("Slot") })
    DuSlider(slot.biasX, { onValueChange(slot.copy(biasX = it)) }, title = { Text("biasX: ${slot.biasX}") }, valueRange = -1f..1f)
    DuSlider(slot.biasY, { onValueChange(slot.copy(biasY = it)) }, title = { Text("biasY: ${slot.biasY}") }, valueRange = 1f..3f)
    DuSlider(slot.sector, { onValueChange(slot.copy(sector = it)) }, title = { Text("sector: ${slot.sector}") }, valueRange = 0f..120f)
    DuSlider(slot.scale, { onValueChange(slot.copy(scale = it, distance = slot.distance / it * slot.scale)) }, title = { Text("scale: ${slot.scale}") }, valueRange = 1f..2f)
    DuSlider(slot.distance, { onValueChange(slot.copy(distance = it)) }, title = { Text("distance: ${slot.distance}") }, valueRange = 0f..5f)
    DuSlider(slot.direction, { onValueChange(slot.copy(direction = it)) }, title = { Text("direction: ${slot.direction}") }, valueRange = -10f..10f)
}

@Composable
fun SlotPreview(slot: HandSlot, modifier: Modifier = Modifier) = Box(modifier.s, Alignment.Center) {
    remember { Array(6) { nextInt(52) } }.forEachIndexed { idx, card ->
        Card(Modifier.slot(slot.position(idx, 6, 6)), card)
    }
}

@Preview
@Composable
internal fun SlotEditorPreview() = DurakTheme { SlotEditor(HandSlot()) {} }

@Preview(widthDp = 150, heightDp = 150)
@Composable
internal fun SlotPreviewPreview() = DurakTheme { SlotPreview(HandSlot()) }
