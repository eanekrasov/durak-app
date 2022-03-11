package durak.app.custom

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.PlusOne
import androidx.compose.material.icons.twotone.SelectAll
import androidx.compose.material.icons.twotone.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import durak.app.R.string.title_customize_deck
import durak.app.ui.BackAwareAppBar
import durak.app.ui.ReorderableCards
import durak.app.ui.TileColumn
import durak.app.utils.p8

@Composable
fun CustomTalon(
    vm: CustomViewModel = hiltViewModel(), onBack: () -> Unit = {}
) = Scaffold(
    topBar = {
        BackAwareAppBar({ Text(stringResource(title_customize_deck)) }, onBack) {
            if (vm.talon.isNotEmpty()) IconButton(vm::shuffleTalon, Modifier.p8) { Icon(TwoTone.Shuffle, "Shuffle") }
            if (vm.unused.isNotEmpty()) IconButton(vm::addAllTalon, Modifier.p8) { Icon(TwoTone.SelectAll, "SelectAll") }
            if (vm.unused.isNotEmpty()) IconButton(vm::addRandomTalon, Modifier.p8) { Icon(TwoTone.PlusOne, "PlusOne") }
        }
    },
) {
    TileColumn {
        ReorderableCards(vm.talon, vm.unused) { card -> vm.toggleTalon(card) }
    }
}
