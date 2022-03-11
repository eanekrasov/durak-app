@file:SuppressLint("MissingPermission")

package durak.app.client

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Cancel
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.insets.ui.LocalScaffoldPadding
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import durak.app.R.string.*
import durak.app.bluetooth.Socket
import durak.app.bluetooth.bluetoothPermissions
import durak.app.game.*
import durak.app.ui.*
import durak.app.utils.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.set

val showAppSettings: () -> Unit @Composable get() = LocalContext.current.run {
    { startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", "durak.app", null))) }
}

@Composable
fun ClientIndex(
    entry: NavBackStackEntry,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = rememberMultiplePermissionsState(bluetoothPermissions)
    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
    val vm = hiltViewModel<ClientViewModel>(entry)
    val ctx = entry.rememberComponentContext()
    val adapter = rememberBluetoothAdapter()

    LaunchedEffect(vm.socket) {
        vm.socket?.readNullTerminated(vm.io, { json ->
            when (val event = Json.decodeFromString<ServerEvent>(json)) {
                is EnterGameServerEvent -> vm.enterGame(event, ctx)
                is LeaveGameServerEvent -> vm.leaveGame()
                is GameServerEvent -> vm.game?.updateState(event)
                is LobbyEvent -> vm.update(event)
            }
        }) {
            vm.disconnect()
            onBack()
        }
    }

    when (vm.game) {
        null -> DeckColumn {
            when {
                !state.allPermissionsGranted && state.shouldShowRationale && !doNotShowRationale -> {
                    Text(stringResource(text_permission_rationale))
                    Button({ state.launchMultiplePermissionRequest() }, Modifier.w.p8.z) { Text(stringResource(button_request), Modifier.p8) }
                    Button({ doNotShowRationale = true }, Modifier.w.p8.z) { Text(stringResource(button_do_not_show_rationale), Modifier.p8) }
                }
                !state.allPermissionsGranted -> {
                    Text(stringResource(text_permission_denied))
                    Button(showAppSettings, Modifier.w.p8.z) { Text(stringResource(button_settings), Modifier.p8) }
                }
                vm.socket == null -> {
                    BackAwareAppBar({ Text(stringResource(title_client)) }, {
                        vm.disconnect()
                        onBack()
                    }) {
                        IconButton({ vm.isDiscovering = !vm.isDiscovering }, Modifier.p8) { Icon(if (vm.isDiscovering) TwoTone.Cancel else TwoTone.Refresh, null) }
                    }
                    SideEffect {
                        if (vm.devices.isEmpty()) adapter?.bondedDevices?.forEach { vm.devices[it.address] = it }
                    }
                    if (vm.isDiscovering) adapter?.DiscoveryEffect({ vm.isDiscovering = false }) { vm.devices[it.address] = it }
                    vm.device?.ConnectingEffect(vm.io) {
                        vm.onSocket(Socket(it)?.apply {
                            scope.launch {
                                writeNullTerminated(vm.io, Json.encodeToString<Action>(JoinAction(vm.config.value.title, vm.config.value.subtitle, vm.config.value.avatar))) {}
                            }
                        })
                    }
                    SwipeRefresh(rememberSwipeRefreshState(vm.isDiscovering), { vm.isDiscovering = true }, Modifier.s, indicatorPadding = LocalScaffoldPadding.current) {
                        LazyColumn(Modifier.s, verticalArrangement = spacedBy(16.dp)) {
                            items(vm.devices.values.toList()) {
                                DeviceItem(it.name ?: "noname", it.address, { Icon(TwoTone.Person, null) }, onClick = { vm.connect(it) })
                            }
                        }
                    }
                }
                vm.game == null -> {
                    BackAwareAppBar({ Text(stringResource(title_client)) }, {
                        vm.disconnect()
                        onBack()
                    })
                    LazyColumn(Modifier.s, verticalArrangement = spacedBy(16.dp)) {
                        item { RulesItem(vm.rules) { onNavigate("rules") } }
                        itemsIndexed(vm.players) { idx, player ->
                            DeviceItem(player.body1, player.body2, { PlayerIcon(idx, player, Modifier.size(48.dp)) })
                        }
                    }
                }
            }
        }
        else -> GameUi(vm.game!!) {
            vm.disconnect()
            onBack()
        }
    }
}
