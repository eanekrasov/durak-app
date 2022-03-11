package durak.app.config


enum class Theme(
    internal val isDark: (isDark: Boolean) -> Boolean
) {
    Default({ it }), Light({ false }), Dark({ true });

    val next get() = values().run { elementAt((ordinal + 1) % size) }
    fun next(isDark: Boolean) = if (this != Default) Default else if (isDark) Light else Dark
}

val allThemes = Theme.values()
