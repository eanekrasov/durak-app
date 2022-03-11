package durak.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import durak.app.R.string.*
import durak.app.game.Rules
import durak.app.utils.*

@Composable
fun RulesItem(
    rules: Rules,
    color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    onClick: () -> Unit = {}
) = TileItem(Modifier.clickable { onClick() }, Arrangement.Center) {
    TileMiniIcon { HandSizeIcon(rules.handSize, Modifier.s48, color) }
    TileMiniIcon { Icon(rules.neighborsIcon, null, Modifier.s48, color) }
    TileMiniIcon { Icon(rules.switchTurnIcon, null, Modifier.s48, color) }
    TileMiniIcon { Icon(rules.firstAttack5cardsIcon, null, Modifier.s48, color) }
    TileMiniIcon { Icon(rules.nextRoundAfterIcon, null, Modifier.s48, color) }
    TileMiniIcon { Icon(rules.unlimitedAttackIcon, null, Modifier.s48, color) }
    TileMiniIcon { Icon(rules.trumplessIcon, null, Modifier.s48, color) }
}

@Preview
@Composable
internal fun RulesItemPreview() = DurakTheme { RulesItem(Rules()) }

@Composable
fun RulesEditor(
    rules: Rules,
    setRules: (Rules) -> Unit = {},
    enabled: Boolean = true,
    color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    onBack: () -> Unit = {}
) = Scaffold(
    topBar = { BackAwareAppBar({ TileColumnTitle { Text(stringResource(title_customize_rules)) } }, onBack) },
) {
    TileColumn {
        DuIntSlider(rules.handSize, { setRules(rules.copy(handSize = it)) }, Modifier, { HandSizeIcon(rules.handSize, Modifier.s48, color) }, { Text(stringResource(rule_hand_Size) + " ${rules.handSize}") }, enabled = enabled, valueRange = 0..10)
        DuCheckbox(
            value = rules.neighbors,
            icon = { Icon(rules.neighborsIcon, null, Modifier.s48, color) },
            title = { Text(stringResource(rule_neighbors)) },
            subtitle = { Text(stringResource(rule_neighbors_extra)) },
            enabled = enabled
        ) { setRules(rules.copy(neighbors = it)) }
        DuCheckbox(
            value = rules.switchTurn,
            icon = { Icon(rules.switchTurnIcon, null, Modifier.s48, color) },
            title = { Text(stringResource(rule_switch_turn)) },
            subtitle = { Text(stringResource(rule_switch_turn_extra)) },
            enabled = enabled
        ) { setRules(rules.copy(switchTurn = it)) }
        DuCheckbox(
            value = rules.firstAttack5cards,
            icon = { Icon(rules.firstAttack5cardsIcon, null, Modifier.s48, color) },
            title = { Text(stringResource(rule_first_attack_5_cards)) },
            subtitle = { Text(stringResource(rule_first_attack_5_cards_extra)) },
            enabled = enabled
        ) { setRules(rules.copy(firstAttack5cards = it)) }
        DuCheckbox(
            value = rules.nextRoundAfter,
            icon = { Icon(rules.nextRoundAfterIcon, null, Modifier.s48, color) },
            title = { Text(stringResource(rule_next_round_after)) },
            subtitle = { Text(stringResource(rule_next_round_after_extra)) },
            enabled = enabled
        ) { setRules(rules.copy(nextRoundAfter = it)) }
        DuCheckbox(
            value = rules.unlimitedAttack,
            icon = { Icon(rules.unlimitedAttackIcon, null, Modifier.s48, color) },
            title = { Text(stringResource(rule_unlimited_attack)) },
            subtitle = { Text(stringResource(rule_unlimited_attack_extra)) },
            enabled = enabled
        ) { setRules(rules.copy(unlimitedAttack = it)) }
        DuCheckbox(
            value = rules.trumpless,
            icon = { Icon(rules.trumplessIcon, null, Modifier.s48, color) },
            title = { Text(stringResource(rule_trumpless)) },
            subtitle = { Text(stringResource(rule_trumpless_extra)) },
            enabled = enabled
        ) { setRules(rules.copy(trumpless = it)) }
    }
}

@Preview
@Composable
internal fun RulesEditorPreview() = DurakTheme { RulesEditor(Rules()) }

@Composable
fun RulesIcons(
    rules: Rules,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) = Row(modifier, Arrangement.Center) {
    TileNanoIcon { HandSizeIcon(rules.handSize, Modifier.s32, color) }
    TileNanoIcon { Icon(rules.neighborsIcon, null, Modifier.s32, color) }
    TileNanoIcon { Icon(rules.switchTurnIcon, null, Modifier.s32, color) }
    TileNanoIcon { Icon(rules.firstAttack5cardsIcon, null, Modifier.s32, color) }
    TileNanoIcon { Icon(rules.nextRoundAfterIcon, null, Modifier.s32, color) }
    TileNanoIcon { Icon(rules.unlimitedAttackIcon, null, Modifier.s32, color) }
    TileNanoIcon { Icon(rules.trumplessIcon, null, Modifier.s32, color) }
}

@Preview
@Composable
internal fun RulesIconsPreview() = DurakTheme { RulesIcons(Rules()) }

@Composable
fun HandSizeIcon(
    handSize: Int,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(RulesEmptyCard, null, modifier, color)
    TileItemTitle { Text("$handSize") }
}

@Preview
@Composable
internal fun HandSizeIconPreview() = DurakTheme { TileIcon { HandSizeIcon(6) } }
