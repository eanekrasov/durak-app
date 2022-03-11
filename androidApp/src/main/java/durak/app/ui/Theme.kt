@file:JvmName("ThemeAndroid")

package durak.app.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import durak.app.config.Theme

@Composable
fun DurakTheme(theme: Theme = Theme.Default, content: @Composable () -> Unit) = MaterialTheme(themeColors(theme), themeTypography, themeShapes) {
    Surface(color = colors.background, content = content)
}

internal val themeShapes = Shapes(RoundedCornerShape(4.dp), RoundedCornerShape(8.dp), RoundedCornerShape(16.dp))

internal val themeTypography = Typography(overline = TextStyle(fontWeight = FontWeight.Normal, fontSize = 8.sp, letterSpacing = 1.5.sp))

@Composable
internal fun themeColors(theme: Theme, animated: Boolean = false) = theme.isDark(isSystemInDarkTheme()).let { if (it) darkColors() else lightColors() }.let { colors ->
    when {
        animated -> updateTransition(colors, "themeColors").run {
            Colors(
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "primary") { it.primary }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "primaryVariant") { it.primaryVariant }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "secondary") { it.secondary }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "secondaryVariant") { it.secondaryVariant }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "background") { it.background }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "surface") { it.surface }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "error") { it.error }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "onPrimary") { it.onPrimary }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "onSecondary") { it.onSecondary }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "onBackground") { it.onBackground }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "onSurface") { it.onSurface }.value,
                animateColor({ spring(stiffness = Spring.StiffnessLow) }, "onError") { it.onError }.value,
                animateFloat({ spring(stiffness = Spring.StiffnessLow) }, "isLight") { if (it.isLight) 1f else 0f }.value > 0.5f
            )
        }
        else -> colors
    }
}
