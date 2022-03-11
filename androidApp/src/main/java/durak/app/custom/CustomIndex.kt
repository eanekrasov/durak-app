package durak.app.custom

import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ComponentContext
import durak.app.R
import durak.app.R.string.*
import durak.app.game.GameUi
import durak.app.ui.BackAwareAppBar
import durak.app.ui.DuIntSlider
import durak.app.ui.TileColumn
import durak.app.utils.p8
import durak.app.utils.w
import durak.app.utils.z

@Composable
fun CustomIndex(
    vm: CustomViewModel,
    ctx: ComponentContext,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
) = when (vm.game) {
    null -> Scaffold(
        topBar = {
            BackAwareAppBar({ Text(stringResource(title_customize_index)) }, onBack) {
                IconButton({ onNavigate("rules") }) { Icon(TwoTone.Settings, "Settings") }
                IconButton({ vm.enterGame(ctx) }, enabled = vm.talon.isNotEmpty()) { Icon(TwoTone.Done, "Done") }
            }
        },
    ) {
        TileColumn {
            DuIntSlider(vm.numPlayers, { vm.numPlayers(it) }, Modifier, { Icon(TwoTone.Person, "Person", tint = colors.primary) }, { Text(stringResource(menu_num_players)+ " ${vm.numPlayers}") }, valueRange = 2..6)
            Button({ onNavigate("talon") }, Modifier.w.p8.z) { Text(stringResource(menu_talon), Modifier.p8) }
            vm.hands.forEachIndexed { plr, hand -> Button({ onNavigate("hands/${plr}") }, Modifier.w.p8.z) { Text("p${plr} (${hand.size})", Modifier.p8) } }
        }
    }
    else -> GameUi(vm.game!!) { vm.leaveGame() }
}
