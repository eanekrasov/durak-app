@file:SuppressLint("MissingPermission")

package durak.app.bluetooth

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import java.util.*
import java.util.UUID.fromString

internal val bluetoothPermissions = when {
    SDK_INT >= S -> listOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT, BLUETOOTH_ADVERTISE)
    else -> listOf(BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_COARSE_LOCATION)
}

internal val durakSecureUUID: UUID = fromString("6cdd29bf-6bfc-4e1a-9486-1da0af965bc8")
internal val durakInsecureUUID: UUID = fromString("d7fe08e8-7187-4128-a67a-4e8005f4d050")

fun durakUUID(insecure: Boolean = false): UUID = when (insecure) {
    true -> durakInsecureUUID
    else -> durakSecureUUID
}

fun BluetoothDevice.createRfcommSocket(insecure: Boolean = false): BluetoothSocket = when (insecure) {
    true -> createInsecureRfcommSocketToServiceRecord(durakUUID(insecure))
    else -> createRfcommSocketToServiceRecord(durakUUID(insecure))
}

fun BluetoothAdapter.listenUsingRfcomm(insecure: Boolean = false): BluetoothServerSocket = when (insecure) {
    true -> listenUsingInsecureRfcommWithServiceRecord("durak", durakUUID(insecure))
    else -> listenUsingRfcommWithServiceRecord("durak", durakUUID(insecure))
}
