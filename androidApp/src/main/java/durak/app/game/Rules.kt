package durak.app.game

import kotlinx.serialization.Serializable

@Serializable
data class Rules(
    val handSize: Int = 6,
    val neighbors: Boolean = false,
    val switchTurn: Boolean = false,
    val firstAttack5cards: Boolean = false,
    val nextRoundAfter: Boolean = false,
    val unlimitedAttack: Boolean = false,
    val trumpless: Boolean = false,
)

//fun interface ProhibitionRule<T: Action> { fun GameState.check(action: T): Boolean }
//fun interface PermissionRule<T: Action> { fun GameState.check(action: T): Boolean }
//val FirstAttack5CardsRule = ProhibitionRule<PutCardAction> { pile.isEmpty() && table.cards.size == rules.handSize - 1 }
//val NeighborsOnlyRule = ProhibitionRule<PutCardAction> { active.nextSibling(def) != it.plr && active.nextSibling(it.plr) != def }
