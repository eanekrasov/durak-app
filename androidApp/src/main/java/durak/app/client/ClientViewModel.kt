package durak.app.client

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.decompose.ComponentContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import durak.app.bluetooth.Socket
import durak.app.config.CardsConfig
import durak.app.config.Config
import durak.app.config.slots
import durak.app.di.IODispatcher
import durak.app.game.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    @ApplicationContext context: Context,
    @IODispatcher val io: CoroutineDispatcher,
    configStore: DataStore<Config>,
    private val cardsConfig: CardsConfig,
) : ViewModel() {
    val config = configStore.data.stateIn(viewModelScope, SharingStarted.Eagerly, Config.defaultValue)
    private val ratio = context.resources.configuration.run { screenWidthDp.toFloat() / screenHeightDp.toFloat() }

    var game by mutableStateOf<RemoteGameComponent?>(null)
    val devices = mutableStateMapOf<String, BluetoothDevice>()
    var socket by mutableStateOf<Socket?>(null)
    var isDiscovering by mutableStateOf(true)
    var players by mutableStateOf(listOf<PlayerInfo>())
    var rules by mutableStateOf(Rules())
    var device by mutableStateOf<BluetoothDevice?>(null)

    fun onSocket(it: Socket?) {
        cardsConfig.isAnimated.value = it == null
        socket = it
        if (it == null) device = null
    }

    fun connect(it: BluetoothDevice) {
        isDiscovering = false
        device = it
    }

    fun disconnect() {
        game = null
        socket?.close()
        socket = null
    }

    fun update(event: LobbyEvent) {
        players = event.players
        rules = event.rules
    }

    fun enterGame(event: EnterGameServerEvent, ctx: ComponentContext) {
        game = event.run {
            RemoteGameComponent(socket!!, io, id, state.cards, players, players(state.hands.size, id), config.value.slots(id, state.hands.size, ratio), state.rules, state.table, state, ctx)
        }
    }

    fun leaveGame() {
        game = null
    }
}
