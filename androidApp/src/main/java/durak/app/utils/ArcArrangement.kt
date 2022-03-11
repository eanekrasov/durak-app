@file:Suppress("unused")

package durak.app.utils

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Stable
@Serializable
sealed class ArcArrangement {
    open val space: Degree get() = Degree.Zero
    abstract fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>)

    @Stable
    @Serializable
    object Left : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            place(start, Degree.Zero, spaces, outPositions)
        }

        override fun toString() = "Arc#Left"
    }

    @Stable
    @Serializable
    object Middle : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            place((end + start - spaces.fold(Degree.Zero) { a, b -> a + b }) / 2, Degree.Zero, spaces, outPositions)
        }

        override fun toString() = "Arc#Middle"
    }

    @Stable
    @Serializable
    object Right : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            place(end - spaces.fold(Degree.Zero) { a, b -> a + b }, Degree.Zero, spaces, outPositions)
        }

        override fun toString() = "Arc#Right"
    }

    @Stable
    @Serializable
    object SpaceAround : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            val gapSize = (end - start - spaces.fold(Degree.Zero) { a, b -> a + b }) / (spaces.size + 1)
            place(gapSize / 2 + start, gapSize, spaces, outPositions)
        }

        override fun toString() = "Arc#SpaceAround"
    }

    @Stable
    @Serializable
    object SpaceBetween : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            val gapSize = if (spaces.isNotEmpty()) (end - start - spaces.fold(Degree.Zero) { a, b -> a + b }) / spaces.size else Degree.Zero
            place(start, gapSize, spaces, outPositions)
        }

        override fun toString() = "Arc#SpaceBetween"
    }

    @Stable
    @Serializable
    object SpaceEvenly : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            val gapSize = (end - start - spaces.fold(Degree.Zero) { a, b -> a + b }) / (spaces.size + 2)
            place(start + gapSize, gapSize, spaces, outPositions)
        }

        override fun toString() = "Arc#SpaceEvenly"
    }

    @Immutable
    @Serializable
    data class SpacedAligned(override val space: Degree = Degree.Zero, val rtlMirror: Boolean = false, val alignment: Alignment.Vertical? = null) : ArcArrangement() {
        override fun arrange(start: Degree, end: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            if (spaces.isEmpty()) return
            var occupied = Degree.Zero
            var lastSpace = Degree.Zero
            for (index in if (!rtlMirror) spaces.indices else spaces.indices.reversed()) {
                outPositions[index] = min(occupied, end - start - spaces[index]) + start
                lastSpace = min(space, end - start - outPositions[index] - spaces[index])
                occupied = outPositions[index] + spaces[index] + lastSpace
            }
            occupied -= lastSpace
            if (alignment != null && occupied < end - start) {
                val groupPosition = alignment.align(0, (end - start - occupied).value.roundToInt()).deg
                for (index in outPositions.indices) {
                    outPositions[index] += groupPosition
                }
            }
        }

        override fun toString() = "Arc#spacedAligned($space, $alignment)"
    }

    companion object {
        internal fun place(initial: Degree, gap: Degree, spaces: Array<Degree>, outPositions: Array<Degree>) {
            var current = initial
            spaces.forEachIndexed { index, space ->
                outPositions[index] = current
                current += space + gap
            }
            outPositions[spaces.size] = current
        }

        @Stable
        fun spacedBy(space: Degree) = SpacedAligned(space, true)

        @Stable
        fun spacedBy(space: Degree, alignment: Alignment.Vertical) = SpacedAligned(space, false, alignment)

        @Stable
        fun aligned(alignment: Alignment.Vertical) = SpacedAligned(Degree.Zero, false, alignment)
    }
}
