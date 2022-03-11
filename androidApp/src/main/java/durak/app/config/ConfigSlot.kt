package durak.app.config

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import durak.app.R.string.title_slot
import durak.app.ui.BackAwareAppBar
import durak.app.ui.SlotEditor
import durak.app.ui.SlotPreview
import durak.app.utils.movable
import durak.app.utils.p8
import durak.app.utils.s
import durak.app.utils.w

@Composable
fun ConfigSlot(onBack: () -> Unit) = Box(Modifier.s, Center) {
    val config = LocalConfig.current
    val screenSize = LocalConfiguration.current.run { DpSize(screenWidthDp.dp, screenHeightDp.dp) }
    var slot by remember(config.value.slot) { mutableStateOf(config.value.slot) }
    SlotPreview(slot, Modifier.movable { (x, y) ->
        slot = slot.copy(biasX = slot.biasX + x * slot.scale * 2f / screenSize.width.toPx(), biasY = slot.biasY + y * slot.scale * 2f / screenSize.height.toPx())
        config.updateConfig { copy(slot = slot) }
    })
    Surface(Modifier.w.p8.align(TopCenter), shape = shapes.small, elevation = 8.dp) {
        Column {
            BackAwareAppBar({ Text(stringResource(title_slot)) }, onBack, actions = {
                IconButton({ config.updateConfig { copy(slot = slot) }; onBack() }) { Icon(TwoTone.Done, "Done") }
            })
            SlotEditor(slot) { slot = it }
        }
    }
}
