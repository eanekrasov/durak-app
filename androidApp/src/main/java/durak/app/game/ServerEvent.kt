package durak.app.game

import kotlinx.serialization.Serializable

@Serializable
sealed class ServerEvent

@Serializable
data class EnterGameServerEvent(val state: GameState, val id: PlayerId) : ServerEvent()

@Serializable
object LeaveGameServerEvent : ServerEvent()

@Serializable
data class GameServerEvent(val state: GameState, val effect: Effect) : ServerEvent()

@Serializable
data class LobbyEvent(val players: List<PlayerInfo>, val rules: Rules, val id: PlayerId) : ServerEvent()
