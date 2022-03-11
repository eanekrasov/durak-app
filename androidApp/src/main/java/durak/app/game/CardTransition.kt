package durak.app.game

import androidx.compose.animation.core.Spring.DampingRatioNoBouncy
import androidx.compose.animation.core.Spring.StiffnessVeryLow
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import durak.app.config.LocalCardScale
import durak.app.utils.Degree
import durak.app.utils.animateDegree
import durak.app.utils.animateDpOffset
import durak.app.utils.deg

data class CardTransition(
    val offset: State<DpOffset>,
    val angleY: State<Degree>,
    val angleZ: State<Degree>,
    val scale: State<Float>,
    val elevation: State<Float>,
    val zIndex: State<Float>,
    val visible: State<Boolean>,
)

@Composable
fun updateCardTransition(card: Card) = updateTransition(stateOf(card).value, "card").run {
    val screenSize = LocalConfiguration.current.run { DpSize(screenWidthDp.dp, screenHeightDp.dp) }
    val cardScale = LocalCardScale.current
    val offset = animateDpOffset({ spring(DampingRatioNoBouncy, StiffnessVeryLow) }, "bias") { it.offset(screenSize, cardScale) }
    val angleY = animateDegree({ spring(DampingRatioNoBouncy, StiffnessVeryLow) }, "angleY") { it.angleY.deg }
    val angleZ = animateDegree({ spring(DampingRatioNoBouncy, StiffnessVeryLow) }, "angleZ") { it.angleZ.deg }
    val scale = animateFloat({ spring(DampingRatioNoBouncy, StiffnessVeryLow) }, "scale") { it.scale * cardScale }
    val elevation = animateFloat({ spring(DampingRatioNoBouncy, StiffnessVeryLow) }, "elevation") { it.elevation }
    val zIndex = animateFloat({ spring(DampingRatioNoBouncy, StiffnessVeryLow) }, "zIndex") { it.zIndex }
    val visible = derivedStateOf { angleY.value < Degree.PI2 }
    remember(this) { CardTransition(offset, angleY, angleZ, scale, elevation, zIndex, visible) }
}
