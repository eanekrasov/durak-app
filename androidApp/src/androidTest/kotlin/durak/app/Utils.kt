package durak.app

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog

/**
 * Used to debug the semantic tree.
 */
@Suppress("unused")
fun ComposeTestRule.dumpSemanticNodes() = onRoot().printToLog("DurakLog")
