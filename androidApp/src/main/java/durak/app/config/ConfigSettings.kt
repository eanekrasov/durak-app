package durak.app.config

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import durak.app.R.string.*
import durak.app.game.*
import durak.app.ui.*
import durak.app.utils.*
import kotlin.math.roundToInt

@Composable
fun ConfigSettings(onBack: () -> Unit) = DeckScaffold({ Text(stringResource(title_settings)) }, onBack) {
    TileColumn {
        val config = LocalConfig.current
        DuAvatarField(config.value.avatar) { config.updateConfig { copy(avatar = it) } }
        DuTextField(config.value.title, { config.updateConfig { copy(title = it) } }, Modifier.w, { Icon(TwoTone.Title, "Title") }, { Text(stringResource(config_title)) })
        DuTextField(config.value.subtitle, { config.updateConfig { copy(subtitle = it) } }, Modifier.w, { Icon(TwoTone.Title, "Title") }, { Text(stringResource(config_subtitle)) })
        DuHorizontalRadioGroup(Modifier.w, allThemes.indexOf(config.value.theme), { Text(stringResource(config_theme)) }, allThemes.map { "$it" }) { config.updateConfig { copy(theme = allThemes.elementAt(it)) } }
        DuList(config.value.theme, allThemes.toList(), Modifier.w, { config.updateConfig { copy(theme = it) } }, { Text(stringResource(config_theme)) }, { Icon(TwoTone.DarkMode, "DarkMode") })
        var deckValue by remember(config.value.deck) { mutableStateOf(config.value.deck) }
        DuIntSlider(deckValue.ordinal, { deckValue = allDecks.elementAt(it) }, Modifier.p8, { Icon(TwoTone.Height, "Height") }, { Text(stringResource(config_deck)) }, valueRange = allDecks.indices, onValueChangeFinished = { config.updateConfig { copy(deck = deckValue) } })
        var scaleValue by remember(config.value.scale) { mutableStateOf(config.value.scale) }
        DuIntSlider((scaleValue * 10f).roundToInt(), { scaleValue = it / 10f }, Modifier.p8, { Icon(TwoTone.AspectRatio, "AspectRatio") }, { Text(stringResource(config_scale)) }, valueRange = 7..13, onValueChangeFinished = { config.updateConfig { copy(scale = scaleValue) } })
        DuHorizontalRadioGroup(Modifier.w, allCardBacks.indexOf(config.value.back), { Text(stringResource(config_back)) }, allCardBacks.map { "$it" }) { config.updateConfig { copy(back = allCardBacks.elementAt(it)) } }
        DuList(config.value.back, allCardBacks.toList(), Modifier.w, { config.updateConfig { copy(back = it) } }, { Text(stringResource(config_back)) }, { Icon(TwoTone.Style, "Style") })
    }
}
