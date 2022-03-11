@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DuList(
    value: Int,
    onValueChange: (Int) -> Unit,
    items: List<String>,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    useSelectedValueAsSubtitle: Boolean = true,
    subtitle: (@Composable () -> Unit)? = null,
    closeDialogDelay: Long = 200,
    action: (@Composable () -> Unit)? = null,
) {
    if (value >= items.size) throw IndexOutOfBoundsException("Current value for $title list setting cannot be grater than items size")
    var showDialog by remember { mutableStateOf(false) }
    TileItem(modifier.clickable { showDialog = true }, icon, title, if (value >= 0 && useSelectedValueAsSubtitle) ({ Text(items[value]) }) else subtitle, action)
    if (showDialog) AlertDialog({ showDialog = false }, {
        Column {
            val coroutineScope = rememberCoroutineScope()
            items.forEachIndexed { index, item ->
                DuRadioButton(index, item, value) { selectedIndex ->
                    coroutineScope.launch {
                        onValueChange(selectedIndex)
                        delay(closeDialogDelay)
                        showDialog = false
                    }
                }
            }
        }
    }, Modifier, title, subtitle)
}

@Composable
fun <T> DuList(
    value: T,
    items: List<T>,
    modifier: Modifier = Modifier,
    onValueChange: (T) -> Unit,
    title: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    useSelectedValueAsSubtitle: Boolean = true,
    subtitle: (@Composable () -> Unit)? = null,
    closeDialogDelay: Long = 200,
    action: (@Composable () -> Unit)? = null,
) = DuList(items.indexOf(value), { onValueChange(items.elementAt(it)) }, items.map { "$it" }, modifier, title, icon, useSelectedValueAsSubtitle, subtitle, closeDialogDelay, action)

@Composable
fun DuList(
    modifier: Modifier = Modifier,
    items: List<String>,
    state: DuValueState<Int> = rememberIntDuState(),
    onValueChange: (Int) -> Unit = {},
    title: @Composable () -> Unit = {},
    icon: (@Composable () -> Unit)? = null,
    useSelectedValueAsSubtitle: Boolean = true,
    subtitle: (@Composable () -> Unit)? = null,
    closeDialogDelay: Long = 200,
    action: (@Composable () -> Unit)? = null,
) = DuList(state.value, { selectedIndex -> state.value = selectedIndex; onValueChange(selectedIndex) }, items, modifier, title, icon, useSelectedValueAsSubtitle, subtitle, closeDialogDelay, action)

@Preview
@Composable
internal fun DuLinkPreview() = DurakTheme { DuList(icon = { Icon(TwoTone.Clear, "Clear") }, items = listOf("Banana", "Kiwi", "Pineapple"), title = { Text("Hello") }, subtitle = { Text("This is a longer text") }) }
