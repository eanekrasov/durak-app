package durak.app.game

import durak.app.bluetooth.Socket

typealias Players = List<Player>

sealed class Player(val id: PlayerId)

object LocalPlayer : Player(0)
class AiPlayer(id: PlayerId) : Player(id)
class RemotePlayer(id: PlayerId, val socket: Socket) : Player(id)

fun players(numPlayers: Int, plrId: Int = 0) = (0 until numPlayers).map { plr -> if (plr != plrId) AiPlayer(plr) else LocalPlayer }

