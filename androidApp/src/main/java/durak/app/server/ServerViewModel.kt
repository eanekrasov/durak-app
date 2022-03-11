package durak.app.server

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
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
import durak.app.ui.ItemPosition
import durak.app.ui.move
import durak.app.utils.fastForEach
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    @IODispatcher val io: CoroutineDispatcher,
    configStore: DataStore<Config>,
    private val cardsConfig: CardsConfig,
) : ViewModel() {
    private val config = configStore.data.stateIn(viewModelScope, SharingStarted.Eagerly, Config.defaultValue)
    private val screenWidthDp = context.resources.configuration.screenWidthDp.toFloat()
    private val screenHeightDp = context.resources.configuration.screenHeightDp.toFloat()
    private val bluetoothManager = context.getSystemService<BluetoothManager>()
    val adapter = bluetoothManager?.adapter
    var isAdvertising by mutableStateOf(false)
    var rules by mutableStateOf(config.value.rules)
    var game by mutableStateOf<LocalGameComponent?>(null)
    val me = PlayerInfo(null, body1 = config.value.title, body2 = config.value.subtitle, avatar = config.value.avatar)
    val infos = mutableStateListOf(me)
    val clients = mutableStateListOf<Socket>()

    fun move(a: ItemPosition, b: ItemPosition) {
        infos.move(infos.indexOfFirst { it == a.key }, infos.indexOfFirst { it == b.key })
    }

    fun updateRules(value: Rules) {
        rules = value
        notifyClients()
    }

    fun enterGame(ctx: ComponentContext) {
        cardsConfig.isAnimated.value = false
        val plrId = infos.indexOf(me)
        val table = Table.initial(screenWidthDp, screenHeightDp, config.value.scale)
        game = LocalGameComponent(io, plrId, config.value.deck.cards, infos, buildPlayers(plrId), buildSlots(plrId, infos.size), rules, table, NoState, ctx)
    }

    fun leaveGame() {
        game = null
        cardsConfig.isAnimated.value = true
    }

    fun buildLoad(ctx: ComponentContext): Game {
        cardsConfig.isAnimated.value = false
        val plrId = infos.indexOf(me)
        val save = config.value.save!!
        val players = (0 until save.hands.size).map { idx -> if (idx == plrId) LocalPlayer else AiPlayer(idx) }
        return LocalGameComponent(io, plrId, save.cards, infos, players, buildSlots(plrId, save.hands.size), save.rules, save.table, save, ctx).also { game = it }
    }

    fun addPlayer(client: Socket?) {
        if (game == null) {
            when (client) {
                null -> infos.add(PlayerInfo())
                else -> addClient(client, false)
            }
            notifyClients()
        }
    }

    fun removePlayer(info: PlayerInfo) {
        if (game == null) {
            when (val client = clientOf(info.address)) {
                null -> infos.remove(info)
                else -> removeClient(client, false)
            }
            notifyClients()
        }
    }

    fun addClient(client: Socket, notify: Boolean = true) {
        if (game == null) {
            clients.add(client)
            update(client.address, Triple(client.name, client.address, null))
            if (notify) notifyClients()
        }
    }

    fun removeClient(client: Socket, notify: Boolean = true) {
        game = null
        update(client.address, null)
        clients.remove(client)
        client.close()
        if (notify) notifyClients()
    }

    fun setInfo(address: String, body1: String, body2: String, avatar: String?) {
        if (game == null) {
            update(address, Triple(body1, body2, avatar))
            notifyClients()
        }
    }

    fun closeAll() = clients.fastForEach { client -> removeClient(client, false) }

    private fun clientOf(address: String?) = clients.firstOrNull { it.address == address }

    private fun indexOf(address: String) = infos.indexOfFirst { it.address == address }

    private fun notifyClients() = clients.fastForEach { client -> client.notify(LobbyEvent(infos, rules, indexOf(client.address))) }

    private fun Socket.notify(event: ServerEvent) = viewModelScope.launch { writeNullTerminated(io, Json.encodeToString(event)) { removeClient(this@notify) } }

    private fun update(address: String, body: Triple<String, String, String?>?) {
        when (val old = infos.firstOrNull { it.address == address }) {
            null -> when (body) {
                null -> Unit
                else -> infos.add(PlayerInfo(address, body.first, body.second, body.third))
            }
            else -> when (body) {
                null -> infos.remove(old)
                else -> infos[infos.indexOf(old)] = PlayerInfo(address, body.first, body.second, body.third)
            }
        }
    }

    private fun buildPlayers(plrId: Int) = infos.mapIndexed { idx, info ->
        val client = clientOf(info.address)
        when {
            idx == plrId -> LocalPlayer
            client == null -> AiPlayer(idx)
            else -> RemotePlayer(idx, client)
        }
    }

    private fun buildSlots(plrId: Int, numPlayers: Int) = config.value.slots(plrId, numPlayers, screenWidthDp / screenHeightDp)
}
