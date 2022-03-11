@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DuCheckbox(
    value: Boolean,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    subtitle: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) = TileItem(
    modifier.toggleable(value, enabled, Checkbox) { onCheckedChange(!value) },
    icon, title, subtitle
) { Checkbox(value, onCheckedChange, enabled = enabled) }

@Composable
fun DuCheckbox(
    modifier: Modifier = Modifier,
    state: DuValueState<Boolean> = rememberBooleanDuState(),
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) = DuCheckbox(state.value, modifier, icon, title, subtitle, enabled) { boolean ->
    state.value = boolean
    onCheckedChange(state.value)
}

@Preview
@Composable
internal fun SettingsCheckboxPreview() = DurakTheme { DuCheckbox(icon = { Icon(TwoTone.Clear, "Clear") }, title = { Text("Hello") }, subtitle = { Text("This is a longer text") }) }
