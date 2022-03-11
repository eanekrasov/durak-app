package durak.app.game

enum class Deck(val ranks: Int) {
    Deck24(6), Deck28(7), Deck32(8), Deck36(9), Deck40(10), Deck44(11), Deck48(12), Deck52(13);
}

val Deck.cards get() = allCards.takeLast(ranks * 4)

val allDecks = Deck.values()
