package durak.app.server

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import durak.app.R.string.title_server
import durak.app.bluetooth.Socket
import durak.app.game.Action
import durak.app.game.GameUi
import durak.app.game.JoinAction
import durak.app.ui.*
import durak.app.utils.fastForEach
import durak.app.utils.s
import durak.app.utils.s48
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun ServerIndex(vm: ServerViewModel, ctx: ComponentContext, onNavigate: (String) -> Unit, onBack: () -> Unit) {
    vm.clients.fastForEach { client ->
        key(client) {
            LaunchedEffect(client) {
                client.readNullTerminated(vm.io, { json ->
                    when (val action = Json.decodeFromString<Action>(json)) {
                        is JoinAction -> vm.setInfo(client.address, action.body1, action.body2, action.avatar)
                        else -> vm.game?.transition(action)
                    }
                }) { vm.removeClient(client) }
            }
        }
    }
    when {
        vm.game != null -> GameUi(vm.game!!) { vm.leaveGame() }
        else -> DeckColumn {
            if (vm.isAdvertising) vm.adapter?.AdvertiserEffect(vm.io) { vm.addClient(Socket(it)) }
            BackAwareAppBar({ Text(stringResource(title_server)) }, {
                vm.closeAll()
                onBack()
            }) {
                IconButton({ vm.addPlayer(null) }) { Icon(TwoTone.Add, "+ ai") }
                IconButton({ vm.enterGame(ctx) }, enabled = vm.infos.size > 1) { Icon(TwoTone.Done, "Done") }
            }
            val state = rememberReorderState()
            LazyColumn(Modifier.s.reorderable(state, { a, b -> vm.move(a, b) }, { i -> vm.infos.any { it == i.key } }), state.listState, verticalArrangement = spacedBy(16.dp)) {
                stickyHeader { RulesItem(vm.rules) { onNavigate("rules") } }
                itemsIndexed(vm.infos, { _, it -> it }) { idx, player ->
                    DeviceItem(
                        player.body1, player.body2, { PlayerIcon(idx, player, Modifier.s48) },
                        Modifier
                            .draggedItem(state.offsetByKey(player))
                            .detectReorderAfterLongPress(state),
                        if (player != vm.me) ({ vm.removePlayer(player) }) else null
                    )
                }
                if (vm.adapter != null) item { BluetoothController(vm.isAdvertising) { vm.isAdvertising = it } }
            }
        }
    }
}
