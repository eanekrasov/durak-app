package durak.app.utils

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.PathBuilder
import durak.app.game.Rules
import kotlin.math.sqrt

val Rules.neighborsIcon get() = if (neighbors) NeighborsOn else NeighborsOff
val NeighborsOn = attackNeighbors("NeighborsOn", false)
val NeighborsOff = attackNeighbors("NeighborsOff", true)
val Rules.switchTurnIcon get() = if (switchTurn) SwitchTurnOn else SwitchTurnOff
val SwitchTurnOn = turnSwitchable("SwitchTurnOn", true)
val SwitchTurnOff = turnSwitchable("SwitchTurnOff", false)
val Rules.firstAttack5cardsIcon get() = if (firstAttack5cards) FirstAttack5CardsOn else FirstAttack5CardsOff
val FirstAttack5CardsOn = firstAttack5Cards("FirstAttack5CardsOn", true)
val FirstAttack5CardsOff = firstAttack5Cards("FirstAttack5CardsOff", false)
val Rules.nextRoundAfterIcon get() = if (nextRoundAfter) NextRoundAfterOn else NextRoundAfterOff
val NextRoundAfterOn = nextRoundAfter("NextRoundAfterOn", true)
val NextRoundAfterOff = nextRoundAfter("NextRoundAfterOff", false)
val Rules.unlimitedAttackIcon get() = if (unlimitedAttack) UnlimitedAttackOn else UnlimitedAttackOff
val UnlimitedAttackOn = unlimitedAttack("UnlimitedAttackOn", true)
val UnlimitedAttackOff = unlimitedAttack("UnlimitedAttackOff", false)
val Rules.trumplessIcon get() = if (trumpless) TrumplessOn else TrumplessOff
val TrumplessOn = trumpless("TrumplessOn", true)
val TrumplessOff = trumpless("TrumplessOff", false)

val RulesEmptyCard = materialIcon("TwoTone.RulesEmptyCard") {
    materialPath { card(12f, 12f, -1f, 0f) }
}

private fun attackNeighbors(name: String, on: Boolean, w: Float = 1f, g: Float = 2f) = materialIcon("TwoTone.$name") {
    materialPath {
        halfFrame(12f, 12f, 1f, 0f, w, g, true)
        halfFrame(12f, 12f, -1f, 0f, w, g, !on)
        arrow(8f, 12f, -1f, 0f)
        arrow(16f, 12f, 1f, 0f)
        if (on) arrow(12f, 12f, 0f, -1f)
    }
}

private fun turnSwitchable(name: String, on: Boolean) = materialIcon("TwoTone.$name") {
    materialPath {
        card()
        if (on) refresh() else arrow(16f, 12f, -1f, 0f)
    }
}

private fun firstAttack5Cards(name: String, on: Boolean) = materialIcon("TwoTone.$name") {
    materialPath {
        card()
        if (on) pacifier(2.5f, 2f, 0.018f) else hat(7f, 7f, 10f / 297f)
    }
}

private fun nextRoundAfter(name: String, on: Boolean) = materialIcon("TwoTone.$name") {
    materialPath {
        halfFrame(12f, 12f, 1f, 0f, 1f, 2f, true)
        halfFrame(12f, 12f, -1f, 0f, 1f, 2f, true)
        hat(if (on) 3.5f else 11.5f, 7f, 9f / 297f)
        arrow(if (on) 22f else 10f, 12f, -1f, 0f)
    }
}

private fun unlimitedAttack(name: String, on: Boolean) = materialIcon("TwoTone.$name") {
    materialPath {
        card()
        if (on) infinity(6f, 6f, 0.5f)
    }
}

private fun trumpless(name: String, on: Boolean) = materialIcon("TwoTone.$name") {
    materialPath {
        card()
        suitDiamonds(6f, 10f, 8f / 1099f)
        suitHearts(10f, 10f, 8f / 1099f)
        suitSpades(14f, if (on) 10f else 7f, (if (on) 8f else 12f) / 1099f)
        suitClubs(18f, 10f, 8f / 1099f)
    }
}

private fun PathBuilder.halfFrame(x: Float = 12f, y: Float = 12f, dx: Float = 0f, dy: Float = 1f, w: Float = 1f, g: Float = 2f, solid: Boolean = true) {
    if (solid) {
        path(x, y, dx, dy, listOf(-10f to g, 0f to 7f - g, 20f to 0f, 0f to -7f + g, -w to 0f, 0f to 7f - w - g, -20f + w + w to 0f, 0f to -7f + w + g))
    } else {
        path(x, y, dx, dy, listOf(-10f to g, 0f to 7f - g, 10f - g to 0f, 0f to -w, -10f + w + g to 0f, 0f to -7f + w + g))
        path(x, y, dx, dy, listOf(g to 7f, 10f - g to 0f, 0f to -7f + g, -w to 0f, 0f to 7f - w - g, -10f + w + g to 0f))
    }
}

private fun PathBuilder.arrow(x: Float = 9f, y: Float = 12f, dx: Float = -1f, dy: Float = 0f) {
    path(x, y, dx, dy, listOf(0f to 0f, 4f to -4f, 0.705f to 0.705f, -2.795f to 2.795f, 6.085f to 0f, 0f to 1f, -6.085f to 0f, 2.795f to 2.795f, -0.705f to 0.705f, -4f to -4f))
}

private fun PathBuilder.card(x: Float = 12f, y: Float = 12f, dx: Float = 1f, dy: Float = 0f) {
    path(x, y, dx, dy, listOf(0f to -7f, -10f to 0f, 0f to 14f, 20f to 0f, 0f to -14f, -10f to 0f, 0f to 1f, 9f to 0f, 0f to 12f, -18f to 0f, 0f to -12f, 9f to 0f))
}

private fun PathBuilder.path(cx: Float, cy: Float, dx: Float, dy: Float, list: List<Pair<Float, Float>>) {
    list.forEachIndexed { i, (xi, yi) ->
        val x = xi * dx + yi * dy
        val y = yi * dx + xi * dy
        if (i == 0) moveTo(cx + x, cy + y) else lineToRelative(x, y)
    }
    close()
}

private fun PathBuilder.refresh(x: Float = 12f, y: Float = 12f, r1: Float = 4f, r2: Float = 3f) {
    val sq2 = sqrt(2f) / 2f
    moveTo(x + r1 * sq2, y - r1 * sq2)
    arcToRelative(r1, r1, 270f, true, false, 0f, 2f * r1 * sq2)
    lineToRelative(r2 * sq2 - r1 * sq2, r2 * sq2 - r1 * sq2)
    arcToRelative(r2, r2, 270f, true, true, 0f, -2f * r2 * sq2)
    lineToRelative(r2 * sq2 - r1 * sq2, r1 * sq2 - r2 * sq2)
    horizontalLineToRelative(3f * sq2)
    verticalLineToRelative(-3f * sq2)
    close()
}

private fun PathBuilder.pacifier(x: Float, y: Float, s: Float = 0.012f) {
    moveTo(x + 305.5f * s, y + 657.5f * s)
    curveToRelative(27.2f * s, 69f * s, 85.8f * s, 121.6f * s, 156.3f * s, 143.4f * s)
    curveToRelative(14.8f * s, 25.1f * s, 41.9f * s, 42.1f * s, 73.1f * s, 42.1f * s)
    curveToRelative(87.8f * s, -67.2f * s, 58.2f * s, -17f * s, 73f * s, -42.1f * s) //s58.2f * s,-17f * s,73f * s,-42.1f * s,
    curveToRelative(70.5f * s, -21.8f * s, 129.2f * s, -74.4f * s, 156.3f * s, -143.4f * s)
    curveToRelative(44.4f * s, -3.8f * s, 79.3f * s, -41.2f * s, 79.3f * s, -86.6f * s)
    curveToRelative(0f * s, -45.4f * s, -35f * s, -82.7f * s, -79.3f * s, -86.6f * s)
    curveToRelative(-36.1f * s, -91.8f * s, -127.9f * s, -154.5f * s, -229.4f * s, -154.5f * s)
    curveToRelative(-101.5f * s, 0f * s, -193.2f * s, 62.7f * s, -229.4f * s, 154.5f * s)
    curveToRelative(-44.4f * s, 3.9f * s, -79.3f * s, 41.2f * s, -79.3f * s, 86.6f * s)
    curveTo(x + 226.2f * s, y + 616.3f * s, x + 261.1f * s, y + 653.7f * s, x + 305.5f * s, y + 657.5f * s)
    close()

    // pacifier white
    moveTo(x + 534.9f * s, y + 795.1f * s)
    curveToRelative(-19.6f * s, 0f * s, -35.6f * s, -15.3f * s, -36.9f * s, -34.6f * s)
    curveToRelative(-0.1f * s, -0.9f * s, -0.2f * s, -1.6f * s, -0.2f * s, -2.5f * s)
    curveToRelative(0f * s, -3.9f * s, 0.8f * s, -7.6f * s, 1.9f * s, -11.2f * s)
    curveToRelative(4.8f * s, -15f * s, 18.7f * s, -25.9f * s, 35.2f * s, -25.9f * s)
    curveToRelative(16.5f * s, 0f * s, 30.4f * s, 10.9f * s, 35.2f * s, 25.9f * s)
    curveToRelative(1.1f * s, 3.5f * s, 1.9f * s, 7.3f * s, 1.9f * s, 11.2f * s)
    curveToRelative(0f * s, 0.9f * s, -0.2f * s, 1.7f * s, -0.2f * s, 2.5f * s)
    curveTo(x + 570.4f * s, y + 779.7f * s, x + 554.5f * s, y + 795.1f * s, x + 534.9f * s, y + 795.1f * s)
    close()

    // face white
    moveTo(x + 313.1f * s, y + 532.1f * s)
    curveToRelative(2f * s, 0f * s, 3.9f * s, 0.2f * s, 5.7f * s, 0.5f * s)
    lineToRelative(20.5f * s, 3.1f * s)
    lineToRelative(6f * s, -19.8f * s)
    curveToRelative(15f * s, -49.3f * s, 49.7f * s, -89.4f * s, 94f * s, -113.4f * s)
    curveToRelative(-11.1f * s, 30.2f * s, 7.5f * s, 66f * s, 37.9f * s, 80.3f * s)
    curveToRelative(39.3f * s, 18.5f * s, 103f * s, 20.9f * s, 129.8f * s, -19.3f * s)
    curveToRelative(12.1f * s, -18.2f * s, 1.4f * s, -41.1f * s, -13.3f * s, -53.5f * s)
    curveToRelative(-20.5f * s, -17.4f * s, -50.2f * s, -19.8f * s, -74.6f * s, -10.2f * s)
    curveToRelative(-19.7f * s, 7.8f * s, -11.1f * s, 40f * s, 8.8f * s, 32.1f * s)
    curveToRelative(14.5f * s, -5.8f * s, 27.6f * s, -6.2f * s, 41.5f * s, 1.5f * s)
    curveToRelative(19f * s, 10.5f * s, 1.5f * s, 22.4f * s, -11.8f * s, 26.2f * s)
    curveToRelative(-24.8f * s, 7.1f * s, -61.4f * s, 1.3f * s, -80.8f * s, -16.1f * s)
    curveToRelative(-24.5f * s, -22f * s, 7.1f * s, -46.9f * s, 26.6f * s, -53.8f * s)
    curveToRelative(21.5f * s, -7.5f * s, 45.1f * s, -9.1f * s, 67.8f * s, -8.2f * s)
    curveToRelative(71.6f * s, 13.3f * s, 131.9f * s, 64.8f * s, 153.2f * s, 134.4f * s)
    lineToRelative(6f * s, 19.8f * s)
    lineToRelative(20.5f * s, -3f * s)
    curveToRelative(1.9f * s, -0.3f * s, 3.8f * s, -0.5f * s, 5.7f * s, -0.5f * s)
    curveToRelative(21.5f * s, 0f * s, 38.9f * s, 17.4f * s, 38.9f * s, 38.9f * s)
    curveToRelative(0f * s, 21.5f * s, -17.5f * s, 38.9f * s, -38.9f * s, 38.9f * s)
    curveToRelative(-2f * s, 0f * s, -3.9f * s, -0.2f * s, -5.8f * s, -0.5f * s)
    lineToRelative(-20.4f * s, -2.9f * s)
    lineToRelative(-6f * s, 19.7f * s)
    curveToRelative(-16.3f * s, 53.5f * s, -55.8f * s, 96.3f * s, -105.7f * s, 119.3f * s)
    curveToRelative(-1.4f * s, -9.1f * s, -4.1f * s, -17.6f * s, -8.1f * s, -25.5f * s)
    curveToRelative(5.8f * s, -8.1f * s, 9.4f * s, -17.2f * s, 9.4f * s, -27.3f * s)
    curveToRelative(0f * s, -33.7f * s, -36.6f * s, -59.1f * s, -85.1f * s, -59.1f * s)
    curveToRelative(-48.5f * s, 0f * s, -85.1f * s, 25.4f * s, -85.1f * s, 59.1f * s)
    curveToRelative(0f * s, 10f * s, 3.6f * s, 19.1f * s, 9.4f * s, 27.3f * s)
    curveToRelative(-4f * s, 7.9f * s, -6.8f * s, 16.4f * s, -8.1f * s, 25.5f * s)
    curveToRelative(-49.9f * s, -23f * s, -89.4f * s, -65.8f * s, -105.7f * s, -119.3f * s)
    lineToRelative(-6f * s, -19.7f * s)
    lineToRelative(-20.4f * s, 2.9f * s)
    curveToRelative(-1.9f * s, 0.3f * s, -3.9f * s, 0.5f * s, -5.9f * s, 0.5f * s)
    curveToRelative(-21.5f * s, 0f * s, -38.9f * s, -17.5f * s, -38.9f * s, -38.9f * s)
    curveTo(x + 274.2f * s, y + 549.5f * s, x + 291.6f * s, y + 532.1f * s, x + 313.1f * s, y + 532.1f * s)
    close()

    // left eye
    moveTo(x + 473.7f * s, y + 599.5f * s)
    curveToRelative(24.9f * s, 0f * s, 24.9f * s, -38.6f * s, 0f * s, -38.6f * s)
    curveTo(x + 448.8f * s, y + 560.9f * s, x + 448.8f * s, y + 599.5f * s, x + 473.7f * s, y + 599.5f * s)
    close()

    // right eye
    moveTo(x + 596f * s, y + 597.1f * s)
    curveToRelative(24.9f * s, 0f * s, 24.9f * s, -38.6f * s, 0f * s, -38.6f * s)
    curveTo(x + 571.1f * s, y + 558.5f * s, x + 571.1f * s, y + 597.1f * s, x + 596f * s, y + 597.1f * s)
    close()

}

private fun PathBuilder.hat(x: Float, y: Float, s: Float = 0.012f) {
    moveTo(x + 273.272f * s, y + 90.3f * s)
    curveToRelative(-12.094f * s, 0f * s, -22.097f * s, 9.107f * s, -23.543f * s, 20.829f * s)
    curveToRelative(-25.794f * s, 2.513f * s, -42.23f * s, 13.708f * s, -51.792f * s, 23.287f * s)
    curveToRelative(-2.884f * s, 2.888f * s, -5.309f * s, 5.773f * s, -7.331f * s, 8.501f * s)
    curveToRelative(-15.265f * s, -27.26f * s, -25.156f * s, -57.277f * s, -29.468f * s, -71.831f * s)
    curveToRelative(7.008f * s, -4.133f * s, 11.723f * s, -11.763f * s, 11.723f * s, -20.48f * s)
    curveToRelative(0f * s, -13.097f * s, -10.639f * s, -23.752f * s, -23.716f * s, -23.752f * s)
    curveToRelative(-13.09f * s, 0f * s, -23.739f * s, 10.655f * s, -23.739f * s, 23.752f * s)
    curveToRelative(0f * s, 8.721f * s, 4.724f * s, 16.354f * s, 11.741f * s, 20.484f * s)
    curveToRelative(-4.319f * s, 14.559f * s, -14.22f * s, 44.571f * s, -29.484f * s, 71.827f * s)
    curveToRelative(-2.021f * s, -2.729f * s, -4.447f * s, -5.613f * s, -7.331f * s, -8.501f * s)
    curveToRelative(-9.721f * s, -9.737f * s, -26.542f * s, -21.141f * s, -53.075f * s, -23.402f * s)
    curveTo(x + 45.758f * s, y + 99.347f * s, x + 35.782f * s, y + 90.3f * s, x + 23.73f * s, y + 90.3f * s)
    curveTo(x + 10.646f * s, y + 90.3f * s, x + 0f * s, y + 100.957f * s, x + 0f * s, y + 114.056f * s)
    curveToRelative(0f * s, 13.099f * s, 10.646f * s, 23.754f * s, 23.73f * s, 23.754f * s)
    curveToRelative(3.641f * s, 0f * s, 7.092f * s, -0.827f * s, 10.178f * s, -2.301f * s)
    curveToRelative(12.41f * s, 27.988f * s, 31.357f * s, 80.649f * s, 22.324f * s, 107.796f * s)
    curveToRelative(-1.171f * s, 3.517f * s, -0.256f * s, 7.393f * s, 2.363f * s, 10.016f * s)
    curveToRelative(2.808f * s, 2.81f * s, 20.362f * s, 16.826f * s, 90.549f * s, 16.826f * s)
    curveToRelative(70.165f * s, 0f * s, 87.715f * s, -14.018f * s, 90.521f * s, -16.828f * s)
    curveToRelative(2.618f * s, -2.622f * s, 3.532f * s, -6.497f * s, 2.362f * s, -10.013f * s)
    curveToRelative(-8.968f * s, -26.955f * s, 9.717f * s, -79.203f * s, 22.123f * s, -107.323f * s)
    curveToRelative(2.81f * s, 1.176f * s, 5.89f * s, 1.827f * s, 9.12f * s, 1.827f * s)
    curveToRelative(13.084f * s, 0f * s, 23.728f * s, -10.655f * s, 23.728f * s, -23.754f * s)
    curveTo(x + 297f * s, y + 100.957f * s, x + 286.355f * s, y + 90.3f * s, x + 273.272f * s, y + 90.3f * s)
    close()
    moveTo(x + 149.145f * s, y + 46.445f * s)
    curveToRelative(2.274f * s, 0f * s, 4.124f * s, 1.866f * s, 4.124f * s, 4.16f * s)
    curveToRelative(0f * s, 2.295f * s, -1.85f * s, 4.162f * s, -4.124f * s, 4.162f * s)
    curveToRelative(-2.287f * s, 0f * s, -4.147f * s, -1.867f * s, -4.147f * s, -4.162f * s)
    curveTo(x + 144.997f * s, y + 48.312f * s, x + 146.857f * s, y + 46.445f * s, x + 149.145f * s, y + 46.445f * s)
// z
    moveTo(23.73f * s, y + 118.218f * s)
    curveToRelative(-2.282f * s, 0f * s, -4.139f * s, -1.867f * s, -4.139f * s, -4.162f * s)
    curveToRelative(0f * s, -2.296f * s, 1.856f * s, -4.164f * s, 4.139f * s, -4.164f * s)
    curveToRelative(2.28f * s, 0f * s, 4.135f * s, 1.868f * s, 4.135f * s, 4.164f * s)
    curveTo(x + 27.865f * s, y + 116.351f * s, x + 26.011f * s, y + 118.218f * s, x + 23.73f * s, y + 118.218f * s)
    close()
    moveTo(x + 221.441f * s, y + 241.614f * s)
    curveToRelative(-8.016f * s, 3.221f * s, -28.456f * s, 8.94f * s, -72.297f * s, 8.94f * s)
    curveToRelative(-43.715f * s, 0f * s, -64.066f * s, -5.66f * s, -72.309f * s, -9.032f * s)
    curveToRelative(6.268f * s, -34.38f * s, -12.448f * s, -84.697f * s, -23.299f * s, -109.938f * s)
    curveToRelative(34.523f * s, 6.373f * s, 44.08f * s, 32.573f * s, 44.502f * s, 33.779f * s)
    curveToRelative(1.183f * s, 3.553f * s, 4.284f * s, 6.12f * s, 7.995f * s, 6.617f * s)
    curveToRelative(3.714f * s, 0.487f * s, 7.381f * s, -1.166f * s, 9.454f * s, -4.282f * s)
    curveToRelative(15.351f * s, -23.063f * s, 26.521f * s, -49.856f * s, 33.65f * s, -70.024f * s)
    curveToRelative(7.124f * s, 20.169f * s, 18.289f * s, 46.96f * s, 33.641f * s, 70.024f * s)
    curveToRelative(2.074f * s, 3.116f * s, 5.739f * s, 4.77f * s, 9.454f * s, 4.282f * s)
    curveToRelative(3.71f * s, -0.497f * s, 6.813f * s, -3.064f * s, 7.995f * s, -6.616f * s)
    curveToRelative(0.104f * s, -0.309f * s, 9.509f * s, -27.271f * s, 44.495f * s, -33.771f * s)
    curveTo(x + 233.862f * s, y + 156.861f * s, x + 215.129f * s, y + 207.238f * s, x + 221.441f * s, y + 241.614f * s)
    close()
    moveTo(x + 273.272f * s, y + 118.218f * s)
    curveToRelative(-2.281f * s, 0f * s, -4.138f * s, -1.867f * s, -4.138f * s, -4.162f * s)
    curveToRelative(0f * s, -2.296f * s, 1.856f * s, -4.164f * s, 4.138f * s, -4.164f * s)
    curveToRelative(2.281f * s, 0f * s, 4.137f * s, 1.868f * s, 4.137f * s, 4.164f * s)
    curveTo(x + 277.408f * s, y + 116.351f * s, x + 275.553f * s, y + 118.218f * s, x + 273.272f * s, y + 118.218f * s)
    close()
}

private fun PathBuilder.infinity(x: Float, y: Float, s: Float = 0.5f) {
    moveTo(x + 18.6f * s, y + 6.62f * s)
    curveToRelative(-1.44f * s, 0.0f * s, -2.8f * s, 0.56f * s, -3.77f * s, 1.53f * s)
    lineTo(x + 7.8f * s, y + 14.39f * s)
    curveToRelative(-0.64f * s, 0.64f * s, -1.49f * s, 0.99f * s, -2.4f * s, 0.99f * s)
    curveToRelative(-1.87f * s, 0.0f * s, -3.39f * s, -1.51f * s, -3.39f * s, -3.38f * s)
    reflectiveCurveTo(x + 3.53f * s, y + 8.62f * s, x + 5.4f * s, y + 8.62f * s)
    curveToRelative(0.91f * s, 0.0f * s, 1.76f * s, 0.35f * s, 2.44f * s, 1.03f * s)
    lineToRelative(1.13f * s, 1.0f * s)
    lineToRelative(1.51f * s, -1.34f * s)
    lineTo(x + 9.22f * s, y + 8.2f * s)
    curveTo(x + 8.2f * s, y + 7.18f * s, x + 6.84f * s, y + 6.62f * s, x + 5.4f * s, y + 6.62f * s)
    curveTo(x + 2.42f * s, y + 6.62f * s, x + 0.0f * s, y + 9.04f * s, x + 0.0f * s, y + 12.0f * s)
    reflectiveCurveToRelative(2.42f * s, 5.38f * s, 5.4f * s, 5.38f * s)
    curveToRelative(1.44f * s, 0.0f * s, 2.8f * s, -0.56f * s, 3.77f * s, -1.53f * s)
    lineToRelative(7.03f * s, -6.24f * s)
    curveToRelative(0.64f * s, -0.64f * s, 1.49f * s, -0.99f * s, 2.4f * s, -0.99f * s)
    curveToRelative(1.87f * s, 0.0f * s, 3.39f * s, 1.51f * s, 3.39f * s, 3.38f * s)
    reflectiveCurveToRelative(-1.52f * s, 3.38f * s, -3.39f * s, 3.38f * s)
    curveToRelative(-0.9f * s, 0.0f * s, -1.76f * s, -0.35f * s, -2.44f * s, -1.03f * s)
    lineToRelative(-1.14f * s, -1.01f * s)
    lineToRelative(-1.51f * s, 1.34f * s)
    lineToRelative(1.27f * s, 1.12f * s)
    curveToRelative(1.02f * s, 1.01f * s, 2.37f * s, 1.57f * s, 3.82f * s, 1.57f * s)
    curveToRelative(2.98f * s, 0.0f * s, 5.4f * s, -2.41f * s, 5.4f * s, -5.38f * s)
    reflectiveCurveToRelative(-2.42f * s, -5.37f * s, -5.4f * s, -5.37f * s)
    close()
}

private fun PathBuilder.suitDiamonds(x: Float, y: Float, s: Float = 20f / 1099f) {
    moveTo(x, y + 1f)
    lineToRelative(-97.7f * s, 132.5f * s)
    lineToRelative(98f * s, 133f * s)
    lineToRelative(98.1f * s, 133f * s)
    lineToRelative(22.3f * s, -30.3f * s)
    curveToRelative(142.1f * s, -192.6f * s, 173.4f * s, -235.3f * s, 173.2f * s, -235.9f * s)
    curveToRelative(-0.5f * s, -1.6f * s, -194.9f * s, -264.8f * s, -195.5f * s, -264.8f * s)
    curveToRelative(-0.4f * s, -0f * s, -44.7f * s, 59.6f * s, -98.4f * s, 132.5f * s)
    close()
}

private fun PathBuilder.suitClubs(x: Float, y: Float, s: Float = 20f / 1099f) {
    moveTo(x - 1f, y)
    curveToRelative(-24.4f * s, 4.1f * s, -48.1f * s, 20.8f * s, -63.7f * s, 44.9f * s)
    curveToRelative(-9.5f * s, 14.5f * s, -15.7f * s, 29.5f * s, -20.1f * s, 48.6f * s)
    curveToRelative(-2.6f * s, 10.9f * s, -2.8f * s, 13.6f * s, -2.8f * s, 31.4f * s)
    curveToRelative(-0.1f * s, 16.2f * s, 0.3f * s, 21.5f * s, 2.2f * s, 31.5f * s)
    curveToRelative(7.6f * s, 40f * s, 21.5f * s, 76.3f * s, 45.7f * s, 120f * s)
    curveToRelative(12.9f * s, 23.2f * s, 22.2f * s, 38.4f * s, 56f * s, 91f * s)
    curveToRelative(33.4f * s, 51.9f * s, 46.6f * s, 73.6f * s, 60.6f * s, 99f * s)
    curveToRelative(8.8f * s, 16.1f * s, 22.8f * s, 45.2f * s, 27.9f * s, 57.8f * s)
    curveToRelative(1.2f * s, 3.2f * s, 2.4f * s, 5.6f * s, 2.6f * s, 5.4f * s)
    curveToRelative(0.2f * s, -0.2f * s, 3f * s, -6.8f * s, 6.3f * s, -14.8f * s)
    curveToRelative(16.3f * s, -40.1f * s, 35.1f * s, -73.2f * s, 73f * s, -128.9f * s)
    curveToRelative(53f * s, -78f * s, 62.5f * s, -93f * s, 77.3f * s, -121.6f * s)
    curveToRelative(22.1f * s, -42.9f * s, 33.8f * s, -80.5f * s, 38.3f * s, -122.9f * s)
    curveToRelative(2f * s, -19.3f * s, 0.8f * s, -34.5f * s, -4.2f * s, -54f * s)
    curveToRelative(-12.1f * s, -46.5f * s, -43f * s, -79.8f * s, -81.1f * s, -87.1f * s)
    curveToRelative(-32f * s, -6.1f * s, -66.4f * s, 9.7f * s, -88.2f * s, 40.6f * s)
    curveToRelative(-6.7f * s, 9.5f * s, -15.3f * s, 27f * s, -18.7f * s, 37.9f * s)
    lineToRelative(-2.6f * s, 8.4f * s)
    lineToRelative(-2.9f * s, -8.8f * s)
    curveToRelative(-17.4f * s, -53.5f * s, -60.9f * s, -85.8f * s, -105.6f * s, -78.4f * s)
    close()
}

private fun PathBuilder.suitSpades(x: Float, y: Float, s: Float = 20f / 1099f) {
    moveTo(x, y)
    curveToRelative(-1.2f * s, 7.5f * s, -7.4f * s, 24.5f * s, -12.9f * s, 35.6f * s)
    curveToRelative(-15.3f * s, 30.6f * s, -34f * s, 54f * s, -75.6f * s, 94.7f * s)
    curveToRelative(-62.6f * s, 61.1f * s, -74.2f * s, 73.4f * s, -86.8f * s, 92.7f * s)
    curveToRelative(-21.7f * s, 33.1f * s, -24.9f * s, 62.8f * s, -9.8f * s, 92.8f * s)
    curveToRelative(10.4f * s, 20.8f * s, 31.7f * s, 38.8f * s, 55.4f * s, 46.9f * s)
    curveToRelative(32.1f * s, 11f * s, 67.9f * s, 6.4f * s, 95.6f * s, -12.2f * s)
    curveToRelative(7.1f * s, -4.8f * s, 19.2f * s, -16.3f * s, 23.1f * s, -21.8f * s)
    curveToRelative(1.5f * s, -2.2f * s, 2.8f * s, -3.8f * s, 3f * s, -3.6f * s)
    curveToRelative(0.7f * s, 0.6f * s, -5.8f * s, 24.4f * s, -9.8f * s, 36.2f * s)
    curveToRelative(-16.1f * s, 47.8f * s, -42.2f * s, 82.2f * s, -82.9f * s, 109.6f * s)
    curveToRelative(-11.8f * s, 7.9f * s, -21.7f * s, 13.7f * s, -45.4f * s, 26.4f * s)
    curveToRelative(-27f * s, 14.5f * s, -45.5f * s, 25.5f * s, -45.5f * s, 27.2f * s)
    curveToRelative(0f * s, 1.1f * s, 32.8f * s, 1.3f * s, 193f * s, 1.3f * s)
    curveToRelative(160.2f * s, -0f * s, 193f * s, -0.2f * s, 193f * s, -1.3f * s)
    curveToRelative(0f * s, -1.7f * s, -18.7f * s, -12.8f * s, -45.5f * s, -27.2f * s)
    curveToRelative(-22.6f * s, -12.2f * s, -33.2f * s, -18.3f * s, -45.5f * s, -26.6f * s)
    curveToRelative(-44.6f * s, -29.8f * s, -72.5f * s, -69.8f * s, -87.8f * s, -126.1f * s)
    curveToRelative(-2.2f * s, -7.9f * s, -4.2f * s, -15.7f * s, -4.6f * s, -17.3f * s)
    lineToRelative(-0.6f * s, -3f * s)
    lineToRelative(2.2f * s, 3f * s)
    curveToRelative(1.2f * s, 1.6f * s, 5.3f * s, 6.3f * s, 9.2f * s, 10.2f * s)
    curveToRelative(48.5f * s, 50.6f * s, 138.5f * s, 37.3f * s, 167.5f * s, -24.9f * s)
    curveToRelative(4.9f * s, -10.4f * s, 7.1f * s, -18.8f * s, 7.8f * s, -29.7f * s)
    curveToRelative(1.1f * s, -18.8f * s, -9.2f * s, -44.1f * s, -28.2f * s, -69.6f * s)
    curveToRelative(-12f * s, -16f * s, -23.3f * s, -28.3f * s, -61.4f * s, -66.5f * s)
    curveToRelative(-39.7f * s, -39.8f * s, -53.9f * s, -55.3f * s, -68.2f * s, -74.2f * s)
    curveToRelative(-17.9f * s, -23.9f * s, -31.3f * s, -50.3f * s, -35.9f * s, -71.1f * s)
    curveToRelative(-0.7f * s, -3.1f * s, -1.6f * s, -5.7f * s, -2f * s, -5.7f * s)
    curveToRelative(-0.4f * s, -0f * s, -1f * s, 1.9f * s, -1.4f * s, 4.2f * s)
    close()
}

private fun PathBuilder.suitHearts(x: Float, y: Float, s: Float = 20f / 1099f) {
    moveTo(x, y)
    curveToRelative(-11.1f * s, 1.2f * s, -24.2f * s, 5.1f * s, -34.4f * s, 10.4f * s)
    curveToRelative(-52.6f * s, 27.2f * s, -69.3f * s, 94.2f * s, -35.5f * s, 142.7f * s)
    curveToRelative(2.3f * s, 3.4f * s, 7.8f * s, 9.7f * s, 12.1f * s, 14f * s)
    curveToRelative(6.9f * s, 7f * s, 7.4f * s, 7.7f * s, 4.3f * s, 6.4f * s)
    curveToRelative(-15.9f * s, -6.6f * s, -40.1f * s, -7.9f * s, -58.4f * s, -3.1f * s)
    curveToRelative(-49.6f * s, 13f * s, -80.9f * s, 61.5f * s, -72.1f * s, 111.8f * s)
    curveToRelative(6.4f * s, 36.6f * s, 33.1f * s, 66.5f * s, 69.1f * s, 77.3f * s)
    curveToRelative(7.1f * s, 2.1f * s, 11.7f * s, 2.7f * s, 22.8f * s, 3.2f * s)
    curveToRelative(15.4f * s, 0.5f * s, 23.4f * s, -0.5f * s, 35.6f * s, -4.6f * s)
    curveToRelative(24.7f * s, -8.2f * s, 46f * s, -27f * s, 57.1f * s, -50.5f * s)
    curveToRelative(3.6f * s, -7.6f * s, 3.6f * s, -5.7f * s, 0.5f * s, 10f * s)
    curveToRelative(-12.2f * s, 59.6f * s, -31.9f * s, 100.2f * s, -64.7f * s, 133.2f * s)
    curveToRelative(-16.3f * s, 16.4f * s, -36.7f * s, 31.1f * s, -71.9f * s, 51.6f * s)
    curveToRelative(-20.3f * s, 11.9f * s, -39.8f * s, 24.4f * s, -41.9f * s, 26.9f * s)
    curveToRelative(-1.2f * s, 1.5f * s, 14.6f * s, 1.6f * s, 187.3f * s, 1.6f * s)
    curveToRelative(120.3f * s, -0f * s, 188.6f * s, -0.3f * s, 188.6f * s, -1f * s)
    curveToRelative(0f * s, -1.4f * s, -19f * s, -13.6f * s, -47.2f * s, -30.4f * s)
    curveToRelative(-26.2f * s, -15.5f * s, -42f * s, -26.3f * s, -55.5f * s, -37.8f * s)
    curveToRelative(-35.1f * s, -29.8f * s, -59.1f * s, -71.6f * s, -72.4f * s, -126.3f * s)
    curveToRelative(-2.6f * s, -10.9f * s, -6.9f * s, -32.4f * s, -6.9f * s, -34.9f * s)
    curveToRelative(0f * s, -0.5f * s, 2f * s, 3.1f * s, 4.4f * s, 8f * s)
    curveToRelative(12.4f * s, 25.4f * s, 34.8f * s, 44f * s, 62.4f * s, 51.7f * s)
    curveToRelative(6.5f * s, 1.8f * s, 10.4f * s, 2.1f * s, 25.2f * s, 2.2f * s)
    curveToRelative(15.4f * s, -0f * s, 18.5f * s, -0.3f * s, 25.4f * s, -2.3f * s)
    curveToRelative(16.9f * s, -4.9f * s, 31.4f * s, -13.3f * s, 43.4f * s, -25.4f * s)
    curveToRelative(13.2f * s, -13.1f * s, 21.9f * s, -28.4f * s, 26.4f * s, -46.5f * s)
    curveToRelative(3.1f * s, -12.3f * s, 3.1f * s, -34.1f * s, 0f * s, -46f * s)
    curveToRelative(-5.6f * s, -21.6f * s, -17.1f * s, -39.6f * s, -33.7f * s, -52.9f * s)
    curveToRelative(-20.2f * s, -16.3f * s, -47f * s, -24.3f * s, -71.3f * s, -21.4f * s)
    curveToRelative(-8.5f * s, 1f * s, -20.8f * s, 4.1f * s, -26.7f * s, 6.6f * s)
    curveToRelative(-1.1f * s, 0.4f * s, 1.1f * s, -2.1f * s, 4.9f * s, -5.6f * s)
    curveToRelative(35.3f * s, -33.1f * s, 41.6f * s, -85.7f * s, 15f * s, -125.6f * s)
    curveToRelative(-16.2f * s, -24.4f * s, -41.1f * s, -39.6f * s, -70.9f * s, -43.3f * s)
    curveToRelative(-9.9f * s, -1.2f * s, -9.5f * s, -1.2f * s, -21f * s, -0f * s)
    close()
}
