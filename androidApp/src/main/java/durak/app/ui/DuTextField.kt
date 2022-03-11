@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.SortByAlpha
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DuTextField(
    modifier: Modifier = Modifier,
    state: DuValueState<String> = rememberStringDuState(),
    title: @Composable () -> Unit = {},
    icon: @Composable (() -> Unit)? = null,
    useSelectedValuesAsSubtitle: Boolean = true,
    subtitle: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
) = DuTextField(state.value, { state.value = it }, modifier, icon, title, subtitle, useSelectedValuesAsSubtitle, action)

@Composable
fun DuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    subtitle: @Composable (() -> Unit)? = null,
    useSelectedValuesAsSubtitle: Boolean = true,
    action: @Composable (() -> Unit)? = null,
) {
    var text by remember(value) { mutableStateOf(value) }
    var showDialog by remember { mutableStateOf(false) }
    TileItem(modifier.clickable { showDialog = true }, icon, title, if (text != "" && useSelectedValuesAsSubtitle) ({ Text(text) }) else subtitle, action)
    if (showDialog) DuAlertDialog(
        { showDialog = false },
        title,
        {
            Column {
                subtitle?.invoke()
                TextField(text, { text = it })
            }
        },
        { IconButton({ onValueChange(text); showDialog = false }) { Icon(TwoTone.Check, "Check") } },
    )
}

@Preview
@Composable
internal fun DuTextFieldPreview() = DurakTheme { DuTextField(icon = { Icon(TwoTone.SortByAlpha, "SortByAlpha") }, title = { Text("Example") }, subtitle = { Text("No value") }) }
