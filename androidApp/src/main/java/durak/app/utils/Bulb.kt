package durak.app.utils

import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.path
import kotlin.math.sqrt

val Day = bulb("Day", true)
val Night = bulb("Night", false)

private fun bulb(name: String, on: Boolean = false, scale: Float = 0.9f) = materialIcon(name = "TwoTone.$name") {
    path("bulb", null, 1f, SolidColor(Color.Black), 1f, 1.5f * scale, StrokeCap.Round, StrokeJoin.Round, 1f) {
        if (on) {
            val sq2 = sqrt(2f)
            moveTo(12f - 7.6667f * scale, 12f)
            horizontalLineTo(12f - 7f * scale)
            moveTo(12f - 7.6667f * scale / sq2, 12f - 7.6667f * scale / sq2)
            lineTo(12f - 7f * scale / sq2, 12f - 7f * scale / sq2)
            moveTo(12f, 12f - 7.6667f * scale)
            verticalLineTo(12f - 7f * scale)
            moveTo(12f + 7.6667f * scale / sq2, 12f - 7.6667f * scale / sq2)
            lineTo(12f + 7f * scale / sq2, 12f - 7f * scale / sq2)
            moveTo(12f + 7.6667f * scale, 12f)
            horizontalLineTo(12f + 7f * scale)
            close()
        }
        moveTo(12f - 3f * scale, 12f + 4f * scale)
        val xx = listOf(3.8395f, 5.0855f, 4.4116f, 2.0722f, 1.0494f, 3.775f, 5.0753f, 4.4597f, 2.6096f, 1.9681f, 2.0f, 1.0391f, 0.5304f, 1.7893f, 1.9195f, 2.3156f).map { it * scale }
        val yy = listOf(3.3704f, 0.4892f, 2.5767f, 4.6698f, 5.0f, 3.4426f, 0.5856f, 2.4925f, 4.3865f, 5.9018f, 7.5304f, 8.7893f, 9.0f, 8.0391f, 6.4566f, 4.8594f).map { it * scale }
        val cc = listOf(2.9235f, 4.7434f, 4.7726f, 3.0f, 2.1419f, 2.0f, 1.4142f).map { it * scale }
        val cy = listOf(4.0562f, 1.5812f, 1.4908f, 4.0f, 5.3806f, 7.0f, 8.4142f).map { it * scale }
        curveTo(12f - xx[0], 12f + yy[0], 12f - xx[7], 12f + yy[7], 12f - cc[2], 12f + cy[2])
        curveTo(12f - xx[1], 12f + yy[1], 12f - xx[6], 12f - yy[6], 12f - cc[1], 12f - cy[1])
        curveTo(12f - xx[2], 12f - yy[2], 12f - xx[5], 12f - yy[5], 12f - cc[0], 12f - cy[0])
        curveTo(12f - xx[3], 12f - yy[3], 12f - xx[4], 12f - yy[4], 12f, 12f - 5.0f * scale)
        curveTo(12f + xx[4], 12f - yy[4], 12f + xx[3], 12f - yy[3], 12f + cc[0], 12f - cy[0])
        curveTo(12f + xx[5], 12f - yy[5], 12f + xx[2], 12f - yy[2], 12f + cc[1], 12f - cy[1])
        curveTo(12f + xx[6], 12f - yy[6], 12f + xx[1], 12f + yy[1], 12f + cc[2], 12f + cy[2])
        curveTo(12f + xx[7], 12f + yy[7], 12f + xx[0], 12f + yy[0], 12f + cc[3], 12f + cy[3])
        curveTo(12f + xx[8], 12f + yy[8], 12f + xx[15], 12f + yy[15], 12f + cc[4], 12f + cy[4])
        curveTo(12f + xx[9], 12f + yy[9], 12f + xx[14], 12f + yy[14], 12f + cc[5], 12f + cy[5])
        curveTo(12f + xx[10], 12f + yy[10], 12f + xx[13], 12f + yy[13], 12f + cc[6], 12f + cy[6])
        curveTo(12f + xx[11], 12f + yy[11], 12f + xx[12], 12f + yy[12], 12f, 12f + 9.0f * scale)
        curveTo(12f - xx[12], 12f + yy[12], 12f - xx[11], 12f + yy[11], 12f - cc[6], 12f + cy[6])
        curveTo(12f - xx[13], 12f + yy[13], 12f - xx[10], 12f + yy[10], 12f - cc[5], 12f + cy[5])
        curveTo(12f - xx[14], 12f + yy[14], 12f - xx[9], 12f + yy[9], 12f - cc[4], 12f + cy[4])
        curveTo(12f - xx[15], 12f + yy[15], 12f - xx[8], 12f + yy[8], 12f - cc[3], 12f + cy[3])
        close()
        moveTo(12f - 2.3f * scale, 12f + 5f * scale)
        horizontalLineTo(12f + 2.3f * scale)
        close()
    }
}
