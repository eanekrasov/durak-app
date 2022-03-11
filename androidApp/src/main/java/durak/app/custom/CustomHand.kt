package durak.app.custom

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.PlusOne
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import durak.app.R.string.title_customize_hand
import durak.app.game.PlayerId
import durak.app.ui.BackAwareAppBar
import durak.app.ui.ReorderableCards
import durak.app.ui.TileColumn
import durak.app.utils.p8

@Composable
fun CustomHand(
    vm: CustomViewModel = hiltViewModel(), plrId: PlayerId, onBack: () -> Unit = {}
) = Scaffold(
    topBar = {
        BackAwareAppBar({ Text(stringResource(title_customize_hand)) }, onBack) {
            if (vm.talon.isNotEmpty()) IconButton({ vm.randomCard(plrId) }, Modifier.p8) { Icon(TwoTone.PlusOne, "PlusOne") }
        }
    },
) {
    TileColumn {
        ReorderableCards(vm.hands[plrId], vm.talon) { card -> vm.toggleCard(plrId, card) }
    }
}
