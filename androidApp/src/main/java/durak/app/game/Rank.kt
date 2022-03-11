package durak.app.game

enum class Rank(val title: String) {
    Two("2"), Three("3"), Four("4"), Five("5"), Six("6"), Seven("7"), Eight("8"),
    Nine("9"), Ten("10"), Jack("J"), Queen("Q"), King("K"), Ace("A")
}

fun ranks(skip: Int = 0) = Rank.values().drop(skip)
