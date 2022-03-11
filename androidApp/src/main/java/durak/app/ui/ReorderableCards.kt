package durak.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import durak.app.game.Card
import durak.app.game.CardHeightDp
import durak.app.game.CardWidthDp

@Composable
fun ReorderableCards(vararg lists: SnapshotStateList<Card>, toggleCard: (Card) -> Unit = {}) = Column {
    lists.forEach { cards ->
        val state = rememberReorderState()
        LazyRow(Modifier.reorderable(state, { a, b -> cards.move(a.index, b.index) }, orientation = Horizontal).requiredHeight((CardHeightDp / 2).dp), state.listState) {
            items(cards, { it }) { card ->
                Card(Modifier.fracCard(0.5f).draggedItem(state.offsetByKey(card), Horizontal).detectReorderAfterLongPress(state).clickable { toggleCard(card) }, card)
            }
        }
    }
}

fun Modifier.fracCard(fraction: Float) = requiredSize((CardWidthDp * fraction).dp, (CardHeightDp * fraction).dp).scale(fraction).requiredSize(CardWidthDp.dp, CardHeightDp.dp)
