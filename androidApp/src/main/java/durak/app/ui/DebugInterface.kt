package durak.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import durak.app.game.Effect
import durak.app.game.GameState
import durak.app.utils.p8
import durak.app.utils.z
import kotlin.math.min

@Composable
fun DebugInterface(state: GameState, effect: Effect?) = Column(Modifier.p8) {
    var states by remember { mutableStateOf<List<GameState>>(listOf()) }
    SideEffect { if (!states.contains(state)) states = states.takeLast(min(states.size, 3)) + state }
    Text("effect $effect", Modifier.p8.z, style = typography.body1)
    states.forEach { Text(it.debugText(), Modifier.p8, style = typography.body1) }
}

fun GameState.debugText() = listOf(
    "talon $talon\npile $pile\nA $att, D $def",
    *hands.mapIndexed { plr, cards -> "hand $plr $cards" }.toTypedArray(),
    "active: $active, ingame: $undone\ntable $table"
).joinToString("\n")
