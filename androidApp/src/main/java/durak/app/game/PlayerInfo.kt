package durak.app.game

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlayerInfo(
    val address: String? = null,
    val body1: String = aiNames.random(),
    val body2: String = "Android",
    val avatar: String? = null,
) : Parcelable

private val aiNames = listOf(
    "Achiever", "Alpha", "Animus", "Aspect", "Aura", "Beauty", "Brain",
    "Care", "Central", "Companion", "Cosmos", "Creator", "Deus", "Dream", "Enigma", "Flux", "Genesis",
    "Ghost", "Golem", "Guest", "Idea", "Intra", "Lucky", "Nerve", "Patch", "Pixel", "Prism",
    "Signal", "Soul", "Sprite", "Synergy", "Tec", "Thinkerer", "Ware", "Whole", "Witness", "Wonder",
)
