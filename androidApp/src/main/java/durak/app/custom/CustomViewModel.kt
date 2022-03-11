package durak.app.custom

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.decompose.ComponentContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import durak.app.config.CardsConfig
import durak.app.config.Config
import durak.app.config.slots
import durak.app.di.IODispatcher
import durak.app.game.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CustomViewModel @Inject constructor(
    @IODispatcher val io: CoroutineDispatcher,
    @ApplicationContext context: Context,
    configStore: DataStore<Config>,
    private val cardsConfig: CardsConfig,
) : ViewModel() {
    private val config = configStore.data.stateIn(viewModelScope, SharingStarted.Eagerly, Config.defaultValue)
    private val screenSize = context.resources.configuration.run { DpSize(screenWidthDp.dp, screenHeightDp.dp) }
    var rules by mutableStateOf(config.value.rules)
    var numPlayers by mutableStateOf(3)
    var hands = (0 until numPlayers).map { idx -> Config.defaultValue.deck.cards.drop(idx * rules.handSize).take(rules.handSize).toMutableStateList() }.toMutableStateList()
    val talon = Config.defaultValue.deck.cards.drop(numPlayers * rules.handSize).toMutableStateList()
    val unused = mutableStateListOf<Card>()
    var game by mutableStateOf<Game?>(null)

    fun numPlayers(new: Int) {
        val old = numPlayers
        numPlayers = new
        when {
            old < new -> hands.addAll((0 until (new - old)).map { talon.take(rules.handSize).also { talon.removeAll(it) }.toMutableStateList() })
            old > new -> talon.addAll(hands.drop(new).also { hands.removeAll(it) }.flatten())
        }
    }

    fun toggleCard(plr: Int, card: Card) {
        if (talon.contains(card)) {
            talon.remove(card)
            hands[plr].add(card)
        } else {
            hands[plr].remove(card)
            talon.add(card)
        }
    }

    fun toggleTalon(card: Card) {
        if (talon.contains(card)) {
            talon.remove(card)
            unused.add(card)
        } else {
            unused.remove(card)
            talon.add(card)
        }
    }

    fun addRandomTalon() {
        val card = unused.random()
        unused.remove(card)
        talon.add(card)
    }

    fun addAllTalon() {
        talon.addAll(unused)
        unused.clear()
    }

    fun shuffleTalon() = talon.shuffle()

    fun randomCard(plr: Int) = toggleCard(plr, talon.random())

    fun enterGame(ctx: ComponentContext) {
        cardsConfig.isAnimated.value = false
        val table = Table.initial(screenSize.width.value, screenSize.height.value, config.value.scale)
        val cards = talon + hands.reversed().flatten()
        val state = GameState(cards, rules, hands.map { it.sorted(if (rules.trumpless) null else talon.first().suit) }, isTurn = true, table = table)
        val slots = config.value.slots(0, numPlayers, screenSize.width.value / screenSize.height.value)
        game = LocalGameComponent(io, 0, cards, (0 until numPlayers).map { PlayerInfo() }, players(numPlayers), slots, rules, table, state, ctx)
    }

    fun leaveGame() {
        game = null
    }
}
