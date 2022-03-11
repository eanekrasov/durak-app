package durak.app.game

import androidx.compose.ui.graphics.vector.ImageVector

enum class CardBack(val image: ImageVector) { Blue(BlueBack), Green(GreenBack), Red(RedBack) }

val allCardBacks = CardBack.values()
