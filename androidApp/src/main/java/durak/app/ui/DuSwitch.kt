@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DuSwitch(
    value: Boolean,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    subtitle: @Composable (() -> Unit)? = null,
    onCheckedChange: (Boolean) -> Unit = {},
) = TileItem(
    modifier.toggleable(value, true, Switch) { onCheckedChange(!value) },
    icon, title, subtitle
) { Switch(value, onCheckedChange) }

@Composable
fun DuSwitch(
    modifier: Modifier = Modifier,
    state: DuValueState<Boolean> = rememberBooleanDuState(),
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    subtitle: @Composable (() -> Unit)? = null,
    onCheckedChange: (Boolean) -> Unit = {},
) = DuSwitch(state.value, modifier, icon, title, subtitle) { boolean ->
    state.value = boolean
    onCheckedChange(state.value)
}

@Preview
@Composable
internal fun DuSwitchPreview() = DurakTheme { DuSwitch(icon = { Icon(TwoTone.Clear, "Clear") }, title = { Text("Hello") }, subtitle = { Text("This is a longer text") }) }
