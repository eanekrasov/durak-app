package durak.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.ToggleOff
import androidx.compose.material.icons.twotone.ToggleOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import durak.app.ui.DurakTheme

enum class ExampleState(val icon: ImageVector) {
    Off(TwoTone.ToggleOff), On(TwoTone.ToggleOn)
}

@Composable
fun ExampleButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick) { Icon(icon, "ExampleButton") }
}

@Composable
fun ExampleScreen() = Column(Modifier.padding(8.dp), Arrangement.Center, Alignment.CenterHorizontally) {
    var state by remember { mutableStateOf(ExampleState.Off) }
    Text(state.name)
    Spacer(Modifier.height(8.dp))
    ExampleButton(state.icon) { state = if (state == ExampleState.Off) ExampleState.On else ExampleState.Off }
}

@Preview
@Composable
internal fun ExampleScreenPreview() = DurakTheme { ExampleScreen() }
