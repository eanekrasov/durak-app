package durak.app.config

import androidx.compose.runtime.staticCompositionLocalOf
import durak.app.game.*
import kotlinx.serialization.Serializable

val LocalConfig = staticCompositionLocalOf<ConfigViewModel> { error("LocalConfig is not provided") }
val LocalCardScale = staticCompositionLocalOf { 1f }

@Serializable
data class Config(
    val title: String = "Unknown",
    val subtitle: String = "",
    val avatar: String? = null,
    val rules: Rules = Rules(),
    val scale: Float = 1f,
    val back: CardBack = CardBack.Blue,
    val deck: Deck = Deck.Deck36,
    val slot: HandSlot = playerSlotDefault,
    val theme: Theme = Theme.Default,
    val layout: Layout = Layout(),
    val save: GameState? = null,
) {
    companion object {
        val defaultValue = Config()
    }
}

fun Config.slots(plrId: Int, numPlayers: Int, ratio: Float) = layout.opponents(numPlayers - 1, ratio).run {
    setOf(*drop(numPlayers - 1 - plrId).toTypedArray(), slot, *take(numPlayers - 1 - plrId).toTypedArray())
}
