package durak.app.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import durak.app.R.string.*
import durak.app.config.LocalConfig
import durak.app.ui.*
import durak.app.utils.*

@Composable
fun GameUi(game: Game, onBack: () -> Unit = {}) = Box(Modifier.s, Center) {
    val isMenu by game.isMenu.subscribeAsState()
    BackHandler(isMenu, onBack)
    BackHandler(!isMenu, game::onMenuClick)
    val state = game.currentState.subscribeAsState().value as? GameState
    Cards(game.cards, state?.handOf(game.plrId).orEmpty(), game::onCardDrop, game::onCardClick)
    GameInterface(game)
    game.players.fastForEach { player -> if (player.id != game.plrId) key(player) { PlayerUi(game, player) } }
    if (isMenu) PauseMenu(game, Modifier.align(Center), onBack)
}

@Composable
fun BoxScope.GameInterface(game: Game) = game.currentState.subscribeAsState().value.run {
    if (this is GameState) {
        RulesIcons(game.rules, Modifier.align(TopCenter))
        IconButton(game::onMenuClick, Modifier.p8.z.align(TopEnd)) { Icon(TwoTone.Menu, "Menu", tint = colors.primary) }
        val attSlot = game.slotOf(att)
        val defSlot = game.slotOf(def)
        val attState = attSlot.position(0, 1, 1)
        val defState = defSlot.position(0, 1, 1)
        val takingShapes = remember { hands.indices.map { game.slotOf(it) }.associate { it.direction to takingShape(it.direction) } }
        if (trump != null) TrumpIcon(talon, trump, Modifier.slot(talonSlot.position(0, false), angleZ = 0f.deg, zIndex = 0.5f))
        Icon(TwoTone.Grade, null, Modifier.slot(attState, distance = 1.7f, elevation = 0f).s36, colors.primary)
        Icon(TwoTone.FlashOn, null, Modifier.slot(defState, distance = 1.7f, elevation = 0f).s36, colors.secondary)
        Column(Modifier.align(BiasAlignment(-1f, 0.7f)).width(55.dp)) {
            table.cards.forEach { (card, by) ->
                Text("${card.label}/${by?.rank?.title ?: "-"}", style = typography.caption)
            }
        }
        if (isTurn && !undone.contains(def)) Taking(defState, takingShapes.getValue(defSlot.direction))
        if (isTurn && canSetDone(game.plrId)) IconButton(game::onDoneClick, Modifier.p16.z.align(BiasAlignment(1f, 0.7f))) {
            when (game.plrId == def) {
                true -> Icon(TwoTone.Close, "Close", Modifier.s48, colors.secondary)
                else -> Icon(TwoTone.Done, "Done", Modifier.s48, colors.primary)
            }
        }
    }
}

@Composable
fun PauseMenu(game: Game, modifier: Modifier = Modifier, onExit: () -> Unit = {}) = Surface(modifier.z.p8, color = colors.surface.copy(alpha = medium)) {
    BackHandler(true, game::onMenuClick)
    Column(Modifier.z) {
        val config = LocalConfig.current
        val gameState = game.currentState.subscribeAsState().value as? GameState
        Button(game::onMenuClick, Modifier.w2.p8) {
            Icon(TwoTone.ArrowBack, "ArrowBack")
            Text(stringResource(menu_resume), Modifier.w.p8)
        }
        Button(game::onRestartClick, Modifier.w2.p8) {
            Icon(TwoTone.Refresh, "Refresh")
            Text(stringResource(menu_restart), Modifier.w.p8)
        }
        Button({ config.updateConfig { copy(save = gameState) }.run { onExit() } }, Modifier.w2.p8, enabled = gameState != null) {
            Icon(TwoTone.Save, "Save")
            Text(stringResource(menu_save), Modifier.w.p8)
        }
        Button(onExit, Modifier.w2.p8) {
            Icon(TwoTone.ExitToApp, "ExitToApp")
            Text(stringResource(menu_exit), Modifier.w.p8)
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun GameUiPreview() = DurakTheme { GameUi(GamePreview(LocalConfiguration.current.screenWidthDp.toFloat(), LocalConfiguration.current.screenHeightDp.toFloat())) }
