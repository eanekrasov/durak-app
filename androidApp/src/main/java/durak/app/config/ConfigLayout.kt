package durak.app.config

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import durak.app.R.string.title_layout
import durak.app.ui.BackAwareAppBar
import durak.app.ui.LayoutEditor
import durak.app.ui.LayoutPreview
import durak.app.utils.p8
import durak.app.utils.s
import durak.app.utils.w

@Composable
fun ConfigLayout(onBack: () -> Unit) = Box(Modifier.s, Center) {
    val config = LocalConfig.current
    var layout by remember(config.value.layout) { mutableStateOf(config.value.layout) }
    var numPlayers by remember { mutableStateOf(5) }
    LayoutPreview(layout, numPlayers)
    Surface(Modifier.w.p8.align(BottomCenter), shape = shapes.small, elevation = 8.dp) {
        Column(Modifier) {
            BackAwareAppBar({ Text(stringResource(title_layout)) }, onBack, {
                IconButton({ if (numPlayers > 1) numPlayers-- }) { Icon(TwoTone.Remove, "Remove") }
                IconButton({ if (numPlayers < 5) numPlayers++ }) { Icon(TwoTone.Add, "Add") }
                Spacer(Modifier.aspectRatio(1f))
                IconButton({ config.updateConfig { copy(layout = layout) }; onBack() }) { Icon(TwoTone.Done, "Done") }
            })
            LayoutEditor(layout) { layout = it }
        }
    }
}
