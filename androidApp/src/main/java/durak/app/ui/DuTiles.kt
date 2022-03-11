package durak.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import durak.app.config.LocalConfig
import durak.app.config.Theme
import durak.app.game.cards
import durak.app.game.randomSlot
import durak.app.game.updateStateOf
import durak.app.utils.*

@Composable
fun BackAwareAppBar(
    title: @Composable () -> Unit,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    BackHandler { onBack() }
    TopAppBar(title, Modifier, { IconButton(onBack) { Icon(TwoTone.ArrowBack, null) } }, actions, colors.surface, colors.primary)
}

@Composable
fun DeckScaffold(
    title: @Composable () -> Unit,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) = DeckColumn {
    Scaffold(
        topBar = { BackAwareAppBar(title, onBack, actions) },
        backgroundColor = Color.Transparent,
    ) {
        TileColumn { content() }
    }
}

@Composable
fun DeckColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) = Box(Modifier.s.z, Center) {
    val config = LocalConfig.current
    Cards(config.value.deck.cards)
    IconButton({ config.updateConfig { copy(theme = theme.next) } }, Modifier.align(BottomEnd).z.background(Gray.copy(0.5f), CircleShape)) {
        when (config.value.theme) {
            Theme.Default -> if (isSystemInDarkTheme()) Icon(Night, "Night", Modifier.s48, Color.White) else Icon(Day, "Day", Modifier.s48, Color.Black)
            Theme.Light -> Icon(Day, "Day", Modifier.s48, Color.Black)
            Theme.Dark -> Icon(Night, "Night", Modifier.s48, Color.White)
        }
    }
    Column(Modifier.s.z.p16.then(modifier)) {
        LaunchedEffect(config.cardsConfig.isAnimated.value) {
            while (config.cardsConfig.isAnimated.value) {
                updateStateOf(config.value.deck.cards.random(), 500) { randomSlot.position() }
            }
        }
        content()
    }
}

@Composable
fun TileItem(
    modifier: Modifier = Modifier, icon: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit, subtitle: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) = TileItem(modifier) {
    TileIcon(icon = icon)
    TileItemTexts(Modifier.weight(1f), title, subtitle)
    if (action != null) {
        TileItemDivider()
        TileAction { action() }
    }
}

@Composable
fun TileColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = TileSurface(modifier) {
    Column(Modifier.w) { content() }
}

// region Internal

@Composable
fun TileItem(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit,
) = TileSurface(modifier) {
    Row(Modifier.w.requiredHeight(64.dp), horizontalArrangement, CenterVertically) { content() }
}

@Composable
fun TileIcon(
    modifier: Modifier = Modifier, icon: @Composable (() -> Unit)? = null
) = TileAction(modifier) { if (icon != null) TileMediumAlpha { icon() } }

@Composable
fun TileMiniIcon(
    modifier: Modifier = Modifier, icon: @Composable (() -> Unit)? = null
) = TileMiniAction(modifier) { if (icon != null) TileMediumAlpha { icon() } }

@Composable
fun TileNanoIcon(
    modifier: Modifier = Modifier, icon: @Composable (() -> Unit)? = null
) = TileNanoAction(modifier) { if (icon != null) TileMediumAlpha { icon() } }

@Composable
fun TileItemTexts(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null
) = Column(modifier, spacedBy(2.dp, CenterVertically)) {
    TileItemTitle { title() }
    if (subtitle != null) TileItemSubtitle { subtitle() }
}

@Composable
fun TileHeroTitle(content: @Composable () -> Unit) = ProvideTextStyle(typography.h6) { content() }

@Composable
fun TileColumnTitle(content: @Composable () -> Unit) = ProvideTextStyle(typography.body1.copy(color = colors.primary)) { content() }

@Composable
fun TileItemTitle(content: @Composable () -> Unit) = ProvideTextStyle(typography.body1) { content() }

@Composable
private fun TileItemSubtitle(content: @Composable () -> Unit) = ProvideTextStyle(typography.body2) { TileMediumAlpha { content() } }

@Composable
private fun TileItemDivider() = Divider(Modifier.padding(vertical = 4.dp).height(56.dp).width(1.dp))

@Composable
private fun TileSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Surface(modifier, elevation = 8.dp) { content() }

@Composable
private fun TileAction(
    modifier: Modifier = Modifier, action: @Composable (() -> Unit)? = null
) = Box(modifier.size(64.dp), Center) { if (action != null) action() }

@Composable
private fun TileMiniAction(
    modifier: Modifier = Modifier, action: @Composable (() -> Unit)? = null
) = Box(modifier.size(48.dp), Center) { if (action != null) action() }

@Composable
private fun TileNanoAction(
    modifier: Modifier = Modifier, action: @Composable (() -> Unit)? = null
) = Box(modifier.size(32.dp), Center) { if (action != null) action() }

@Composable
private fun TileMediumAlpha(
    content: @Composable () -> Unit
) = CompositionLocalProvider(LocalContentAlpha provides medium) { content() }

// endregion

// region Previews

@Preview
@Composable
internal fun TileMenuLinkActionPreview() = DurakTheme {
    var state by remember { mutableStateOf(true) }
    TileItem(Modifier, { Icon(TwoTone.Clear, "Clear") }, { Text("Hello") }, { Text("This is a longer text") }, { Checkbox(state, { state = it }) })
}

@Preview
@Composable
internal fun TileColumnPreview() = DurakTheme {
    TileColumn {
        TileColumnTitle { Text("Title") }
        TileAction { Text("Settings group") }
    }
}

@Preview
@Composable
internal fun TileMenuLinkPreview() = DurakTheme { TileItem(Modifier, { Icon(TwoTone.Clear, "Clear") }, { Text("Hello") }, { Text("This is a longer text") }) }

@Preview
@Composable
internal fun TileIconPreview() = DurakTheme { TileIcon { Icon(TwoTone.Clear, "Clear") } }

@Preview
@Composable
internal fun TileIconEmptyPreview() = DurakTheme { TileIcon() }

// endregion
