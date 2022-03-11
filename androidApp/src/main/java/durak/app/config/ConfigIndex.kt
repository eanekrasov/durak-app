package durak.app.config

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import durak.app.R.string.*
import durak.app.ui.DeckScaffold
import durak.app.utils.p8
import durak.app.utils.w
import durak.app.utils.z

@Composable
fun ConfigIndex(base: String, onNavigateClick: (String) -> Unit, onBack: () -> Unit) = DeckScaffold({ Text(stringResource(title_config)) }, onBack) {
    Button({ onNavigateClick("$base/settings") }, Modifier.w.p8.z) { Text(stringResource(menu_settings), Modifier.p8) }
    Button({ onNavigateClick("$base/slot") }, Modifier.w.p8.z) { Text(stringResource(menu_slot), Modifier.p8) }
    Button({ onNavigateClick("$base/layout") }, Modifier.w.p8.z) { Text(stringResource(menu_layout), Modifier.p8) }
    Button({ onNavigateClick("$base/rules") }, Modifier.w.p8.z) { Text(stringResource(menu_rules), Modifier.p8) }
}
