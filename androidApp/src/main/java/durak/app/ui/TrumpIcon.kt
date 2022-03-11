package durak.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import durak.app.game.Suit
import durak.app.utils.p8
import durak.app.utils.z

@Composable
fun TrumpIcon(count: Int, trump: Suit, modifier: Modifier = Modifier) = Box(modifier, Alignment.Center) {
    Text(trump.symbol, Modifier.p8.z, Color(trump.color), style = MaterialTheme.typography.h3)
    Text("$count", Modifier.z, Color.White)
}

@Preview
@Composable
internal fun SpadesPreview() = DurakTheme { TrumpIcon(12, Suit.Spades) }

@Preview
@Composable
internal fun ClubsPreview() = DurakTheme { TrumpIcon(18, Suit.Clubs) }

@Preview
@Composable
internal fun DiamondsPreview() = DurakTheme { TrumpIcon(25, Suit.Diamonds) }

@Preview
@Composable
internal fun HeartsPreview() = DurakTheme { TrumpIcon(36, Suit.Hearts) }
