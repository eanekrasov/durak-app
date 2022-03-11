package durak.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import durak.app.R.string.*
import durak.app.game.CardState
import durak.app.utils.Degree
import durak.app.utils.p8
import durak.app.utils.slot
import durak.app.utils.z
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun Taking(state: CardState, shape: Shape) = Surface(
    Modifier.z.slot(state, 2f, angleZ = Degree.Zero, scale = 1f, cardShape = shape),
    shape = shape,
    color = Color.White,
    contentColor = colors.secondary,
    border = BorderStroke(width = 1.dp, color = colors.secondary),
    elevation = 10.dp
) { Text(stringResource(game_taking), Modifier.p8, fontSize = 30.sp) }

fun takingShape(direction: Float) = GenericShape { size, _ ->
    addPath(Path.combine(PathOperation.Union, Path().apply {
        val r = size.height * 0.4f
        reset()
        arcTo(Rect(0f, 0f, r * 2f, r * 2f), 180.0f, 90.0f, false)
        lineTo(size.width - r * 2f, 0f)
        arcTo(Rect(size.width - r * 2f, 0f, size.width, r * 2f), 270.0f, 90.0f, false)
        lineTo(size.width, size.height - r * 2f)
        arcTo(Rect(size.width - r * 2f, size.height - r * 2f, size.width, size.height), 0.0f, 90.0f, false)
        lineTo(r * 2f, size.height)
        arcTo(Rect(0f, size.height - r * 2f, r * 2f, size.height), 90.0f, 90.0f, false)
        lineTo(0f, r * 2f)
        close()
    }, Path().apply {
        val distance = max(size.width, size.height)
        val angle = direction * PI.toFloat() / 180f
        val lx = distance * sin(angle)
        val ly = distance * -cos(angle)
        lineTo(size.width / 2f - lx, size.height / 2f - ly)
        lineTo(size.width / 2f - ly / 7f, size.height / 2f + lx / 7f)
        lineTo(size.width / 2f + ly / 7f, size.height / 2f - lx / 7f)
        lineTo(size.width / 2f - lx, size.height / 2f - ly)
        close()
    }))
}

@Preview
@Composable
internal fun TakingPreview() = DurakTheme { Taking(CardState(), remember { takingShape(0f) }) }
