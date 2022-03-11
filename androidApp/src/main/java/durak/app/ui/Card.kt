package durak.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import durak.app.config.LocalConfig
import durak.app.game.*
import durak.app.utils.fastForEach
import durak.app.utils.p8
import durak.app.utils.s
import durak.app.utils.slot

@Composable
fun Cards(
    cards: Cards,
    movableCards: Cards = listOf(),
    onDrop: (Card, Pair<Float, Float>) -> Unit = { _, _ -> },
    onClick: (Card) -> Unit = { }
) = cards.fastForEach { card ->
    Card(card, movableCards.contains(card), { (x, y) -> onDrop(card, x.toDp().value to y.toDp().value) }) { onClick(card) }
}

@Composable
private fun Card(
    card: Card,
    movingEnabled: Boolean,
    onDrop: Density.(Offset) -> Unit = {},
    onClick: () -> Unit
) = updateCardTransition(card).let {
    Card(Modifier.slot(it, shapes.small, movingEnabled, onDrop, onClick), card.takeIf { _ -> it.visible.value })
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    card: Card? = null,
) = Surface(modifier.requiredSize(CardWidthDp.dp, CardHeightDp.dp), shapes.small, Color.White, Color.Black, BorderStroke(1.dp, Color.Black)) {
    when (card) {
        null -> Image(LocalConfig.current.value.back.image, "Back", Modifier.s)
        else -> {
            Column(Modifier.s.p8) {
                Text(card.suit.symbol)
                Text(card.rank.title, color = Color(card.suit.color))
            }
            Column(Modifier.rotate(180f).s.p8) {
                Text(card.suit.symbol)
                Text(card.rank.title, color = Color(card.suit.color))
            }
        }
    }
}

@Preview(widthDp = CardWidthDp, heightDp = CardHeightDp)
@Composable
internal fun CardFacePreview() = DurakTheme { Card(Modifier, Card(Rank.Ace, Suit.Clubs)) }

@Preview(widthDp = CardWidthDp, heightDp = CardHeightDp)
@Composable
internal fun CardBackPreview() = DurakTheme { Card(Modifier, null) }
