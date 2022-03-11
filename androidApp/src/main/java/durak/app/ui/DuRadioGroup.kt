@file:Suppress("unused")

package durak.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import durak.app.utils.p16
import durak.app.utils.p8
import durak.app.utils.w

@Composable
fun <T> DuHorizontalRadioGroup(
    modifier: Modifier = Modifier,
    value: T, title: @Composable () -> Unit, items: Array<out T>,
    onValueChange: (T) -> Unit
) = DuHorizontalRadioGroup(modifier, items.indexOf(value), title, items.map { "$it" }) { onValueChange(items.elementAt(it)) }

@Composable
fun <T> DuHorizontalRadioGroup(
    modifier: Modifier = Modifier,
    value: T, items: Array<out T>,
    onValueChange: (T) -> Unit
) = DuHorizontalRadioGroup(modifier, items.indexOf(value), items.map { "$it" }) { onValueChange(items.elementAt(it)) }

@Composable
fun DuHorizontalRadioGroup(modifier: Modifier = Modifier, value: Int, title: @Composable () -> Unit, items: List<String>, onValueChange: (Int) -> Unit) = TileColumn(modifier) {
    TileColumnTitle(title)
    DuHorizontalRadioGroup(Modifier, value, items, onValueChange)
}

@Composable
fun DuHorizontalRadioGroup(modifier: Modifier = Modifier, value: Int, items: List<String>, onValueChange: (Int) -> Unit) = Row(modifier) {
    items.forEachIndexed { index, item ->
        DuRadioButton(index, item, value, onValueChange)
    }
}

@Composable
fun RowScope.DuRadioButton(index: Int, item: String, value: Int, onValueChange: (Int) -> Unit) {
    val isSelected by rememberUpdatedState(newValue = value == index)
    Column(Modifier.p8.selectable(isSelected, role = Role.RadioButton) { if (!isSelected) onValueChange(index) }, horizontalAlignment = CenterHorizontally) {
        RadioButton(isSelected, { if (!isSelected) onValueChange(index) })
        Text(item, Modifier.padding(start = 16.dp), style = typography.body1)
    }
}

@Composable
fun <T> DuVerticalRadioGroup(
    modifier: Modifier = Modifier,
    value: T, title: @Composable () -> Unit, items: Array<out T>,
    onValueChange: (T) -> Unit
) = DuVerticalRadioGroup(modifier, items.indexOf(value), title, items.map { "$it" }) { onValueChange(items.elementAt(it)) }

@Composable
fun <T> DuVerticalRadioGroup(
    modifier: Modifier = Modifier,
    value: T, items: Array<out T>,
    onValueChange: (T) -> Unit
) = DuVerticalRadioGroup(modifier, items.indexOf(value), items.map { "$it" }) { onValueChange(items.elementAt(it)) }

@Composable
fun DuVerticalRadioGroup(modifier: Modifier = Modifier, value: Int, title: @Composable () -> Unit, items: List<String>, onValueChange: (Int) -> Unit) = TileColumn(modifier) {
    TileColumnTitle(title)
    DuVerticalRadioGroup(Modifier, value, items, onValueChange)
}

@Composable
fun DuVerticalRadioGroup(modifier: Modifier = Modifier, value: Int, items: List<String>, onValueChange: (Int) -> Unit) = Column(modifier) {
    items.forEachIndexed { index, item ->
        DuRadioButton(index, item, value, onValueChange)
    }
}

@Composable
fun ColumnScope.DuRadioButton(index: Int, item: String, value: Int, onValueChange: (Int) -> Unit) {
    val isSelected by rememberUpdatedState(newValue = value == index)
    Row(Modifier.w.p16.selectable(role = Role.RadioButton, selected = isSelected, onClick = { if (!isSelected) onValueChange(index) }), verticalAlignment = CenterVertically) {
        RadioButton(isSelected, onClick = { if (!isSelected) onValueChange(index) })
        Text(item, Modifier.padding(start = 16.dp), style = typography.body1)
    }
}
