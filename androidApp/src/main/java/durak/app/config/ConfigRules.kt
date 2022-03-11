package durak.app.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import durak.app.ui.DurakTheme
import durak.app.ui.RulesEditor

@Composable
fun ConfigRules(onBack: () -> Unit = {}) {
    val config = LocalConfig.current
    RulesEditor(config.value.rules, { rules -> config.updateConfig { copy(rules = rules) } }, true, onBack = onBack)
}

@Preview
@Composable
fun ConfigRulesPreview() = DurakTheme { ConfigRules() }
