package durak.app.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import durak.app.ui.DurakTheme

const val CardWidthDp = 70
const val CardHeightDp = 100

fun back(dark: SolidColor, light: SolidColor) = listOf(
    0f to (SolidColor(Color.White) to null), 3f to (null to dark), 15f to (light to null), 18f to (SolidColor(Color.White) to null), 21f to (light to null)
).fold(ImageVector.Builder("Back", CardWidthDp.dp, CardHeightDp.dp, CardWidthDp.toFloat(), CardHeightDp.toFloat())) { builder, (space, colors) ->
    builder.path("back $space", colors.first, 1f, colors.second, 1f, 2f) {
        moveTo(space, space)
        verticalLineTo(CardHeightDp - space)
        horizontalLineTo(CardWidthDp - space)
        verticalLineTo(space)
        horizontalLineTo(space)
        close()
    }
}.build()

val RedBack = back(SolidColor(Color(0xFF7a0000)), SolidColor(Color(0xFF903333)))
val GreenBack = back(SolidColor(Color(0xFF007a00)), SolidColor(Color(0xFF339033)))
val BlueBack = back(SolidColor(Color(0xFF00007a)), SolidColor(Color(0xFF333390)))

@Preview(widthDp = CardWidthDp, heightDp = CardHeightDp)
@Composable
internal fun RedBackPreview() = DurakTheme { Image(RedBack, "Back", Modifier.border(1.dp, Color.Black, shapes.small)) }

@Preview(widthDp = CardWidthDp, heightDp = CardHeightDp)
@Composable
internal fun GreenBackPreview() = DurakTheme { Image(GreenBack, "Back", Modifier.border(1.dp, Color.Black, shapes.small)) }

@Preview(widthDp = CardWidthDp, heightDp = CardHeightDp)
@Composable
internal fun BlueBackPreview() = DurakTheme { Image(BlueBack, "Back", Modifier.border(1.dp, Color.Black, shapes.small)) }
