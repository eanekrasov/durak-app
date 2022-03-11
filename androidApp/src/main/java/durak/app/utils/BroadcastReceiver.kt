package durak.app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberBroadcastReceiver(onReceive: (Intent) -> Unit) = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = onReceive(intent)
}

@Composable
fun BroadcastReceiver(action: String, onReceive: (Intent) -> Unit) = BroadcastReceiver(rememberBroadcastReceiver(onReceive), IntentFilter(action))

@Composable
fun BroadcastReceiver(receiver: BroadcastReceiver, intentFilter: IntentFilter) {
    val activity = LocalContext.current as ComponentActivity
    DisposableEffect(receiver, intentFilter) {
        activity.registerReceiver(receiver, intentFilter)
        onDispose { activity.unregisterReceiver(receiver) }
    }
}
