package durak.app.game

enum class Suit(val symbol: String, val color: Long) {
    Clubs("♣️" /* U+2663 ♣ U+2667 ♧ */, 0xFF000000),
    Diamonds("♦️" /* U+2662 ♦ U+2666 ♢ */, 0xFFFF0000),
    Hearts("♥️" /* U+2661 ♥ U+2665 ♡ */, 0xFFFF0000),
    Spades("♠️" /* U+2660 ♠ U+2664 ♤ */, 0xFF000000);
}

fun suits(skip: Int = 0) = Suit.values().drop(skip)
