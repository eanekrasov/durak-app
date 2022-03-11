package durak.app.server

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Bluetooth
import androidx.compose.material.icons.twotone.BluetoothDisabled
import androidx.compose.material.icons.twotone.BluetoothSearching
import androidx.compose.material.icons.twotone.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import durak.app.R.string.*
import durak.app.bluetooth.bluetoothPermissions
import durak.app.ui.DurakTheme
import durak.app.ui.TileItem

@Composable
fun BluetoothController(
    isAdvertising: Boolean = true, onValueChange: (Boolean) -> Unit = {},
) {
    val permissions = rememberMultiplePermissionsState(bluetoothPermissions)
    TileItem(
        Modifier.clickable { if (permissions.allPermissionsGranted) onValueChange(!isAdvertising) else permissions.launchMultiplePermissionRequest() },
        { Icon(if (!permissions.allPermissionsGranted) TwoTone.BluetoothDisabled else if (isAdvertising) TwoTone.BluetoothSearching else TwoTone.Bluetooth, null) },
        { Text(stringResource(if (isAdvertising) title_bluetooth_active else title_bluetooth)) },
        { Text(stringResource(if (!permissions.allPermissionsGranted) advertising_denied else if (isAdvertising) advertising_active else advertising_inactive)) }
    ) { if (isAdvertising) Icon(TwoTone.Check, "Check") }
}

@Preview
@Composable
fun BluetoothControllerPreview() = DurakTheme { BluetoothController(true) }
