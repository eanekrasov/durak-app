@file:SuppressLint("MissingPermission")

package durak.app.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import durak.app.bluetooth.createRfcommSocket
import durak.app.bluetooth.listenUsingRfcomm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Composable
fun rememberBluetoothAdapter(context: Context = LocalContext.current) = remember { context.getSystemService<BluetoothManager>()?.adapter }

@Composable
fun DeviceItem(
    body1: String, body2: String, icon: @Composable () -> Unit, modifier: Modifier = Modifier, onDelete: (() -> Unit)? = null, onClick: (() -> Unit)? = null,
) = TileItem(
    modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    icon,
    { Text(body1) },
    { Text(body2) },
) {
    if (onDelete != null) IconButton(onDelete) { Icon(TwoTone.Delete, "Delete") }
}

@Composable
fun BluetoothAdapter.AdvertiserEffect(
    io: CoroutineDispatcher,
    insecure: Boolean = false,
    callback: (BluetoothSocket) -> Unit
) = LaunchedEffect(this) {
    listenUsingRfcomm(insecure).use { advertiser ->
        while (true) withContext(io) {
            runCatching { advertiser.accept() }
        }.getOrNull()?.let(callback)
    }
}

@Composable
fun BluetoothAdapter.DiscoveryEffect(onFinished: () -> Unit, callback: (BluetoothDevice) -> Unit) {
    BroadcastReceiver(BluetoothDevice.ACTION_FOUND) { intent -> callback(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!) }
    BroadcastReceiver(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) { onFinished() }
    DisposableEffect(this) {
        startDiscovery()
        onDispose { cancelDiscovery() }
    }
}

@Composable
fun BluetoothDevice.ConnectingEffect(
    io: CoroutineDispatcher,
    insecure: Boolean = false,
    callback: (BluetoothSocket?) -> Unit
) = LaunchedEffect(this) {
    withContext(io) {
        runCatching { createRfcommSocket(insecure).apply { connect() } }
    }.getOrNull().let(callback)
}
