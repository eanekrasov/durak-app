@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import durak.app.utils.w

@Composable
fun DuListMultiSelect(
    modifier: Modifier = Modifier,
    state: DuValueState<Set<Int>> = rememberIntSetDuState(),
    title: @Composable () -> Unit,
    items: List<String>,
    icon: @Composable (() -> Unit)? = null,
    confirmButton: String,
    useSelectedValuesAsSubtitle: Boolean = true,
    subtitle: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
) = DuListMultiSelect(modifier, state.value, { state.value = it }, title, items, icon, confirmButton, useSelectedValuesAsSubtitle, subtitle, action)

@Composable
fun DuListMultiSelect(
    modifier: Modifier = Modifier,
    value: Set<Int> = setOf(),
    onValueChange: (Set<Int>) -> Unit,
    title: @Composable () -> Unit,
    items: List<String>,
    icon: @Composable (() -> Unit)? = null,
    confirmButton: String,
    useSelectedValuesAsSubtitle: Boolean = true,
    subtitle: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
) {
    if (value.any { index -> index >= items.size }) throw IndexOutOfBoundsException("Current indexes for $title list setting cannot be grater than items size")
    var showDialog by remember { mutableStateOf(false) }
    TileItem(modifier.clickable { showDialog = true }, icon, title, if (value.size >= 0 && useSelectedValuesAsSubtitle) ({ Text(value.joinToString { items[it] }) }) else subtitle, action)
    val onAdd: (Int) -> Unit = { selectedIndex -> onValueChange(value.toMutableSet().apply { add(selectedIndex) }) }
    val onRemove: (Int) -> Unit = { selectedIndex -> onValueChange(value.toMutableSet().apply { remove(selectedIndex) }) }
    if (showDialog) DuAlertDialog(
        { showDialog = false },
        title,
        {
            Column {
                subtitle?.invoke()
                items.forEachIndexed { index, item ->
                    val isSelected by rememberUpdatedState(newValue = value.contains(index))
                    Row(
                        Modifier.w.selectable(isSelected, role = Role.Checkbox, onClick = { if (isSelected) onRemove(index) else onAdd(index) }).padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        Text(item, Modifier.weight(1f), style = typography.body1)
                        Checkbox(checked = isSelected, onCheckedChange = { checked -> if (checked) onRemove(index) else onAdd(index) })
                    }
                }
            }
        },
        { TextButton({ showDialog = false }) { ProvideTextStyle(typography.body1.copy(fontFeatureSettings = "c2sc, smcp")) { Text(confirmButton) } } },
    )
}
