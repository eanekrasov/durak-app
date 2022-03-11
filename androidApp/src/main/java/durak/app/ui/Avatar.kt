@file:Suppress("DEPRECATION")

package durak.app.ui

import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory.decodeByteArray
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Base64.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.twotone.Android
import androidx.compose.material.icons.twotone.Bluetooth
import androidx.compose.material.icons.twotone.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import durak.app.R.string.config_avatar
import durak.app.game.*
import durak.app.utils.*
import java.io.ByteArrayOutputStream

@Composable
fun DuAvatarField(
    value: String?, modifier: Modifier = Modifier, onValueChange: (String?) -> Unit = {}
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val pickLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            val dp64 = density.run { 64.dp.roundToPx() }
            val base64 = encodeToString(ByteArrayOutputStream().also {
                val bmp = if (SDK_INT < P) getBitmap(context.contentResolver, uri) else decodeBitmap(createSource(context.contentResolver, uri))
                createScaledBitmap(bmp, dp64, dp64, false).compress(PNG, 100, it)
            }.toByteArray(), DEFAULT)
            onValueChange(base64)
        }
    }
    TileItem(
        modifier.clickable { pickLauncher.launch("image/*") },
        icon = { Avatar(value, Modifier.s64) },
        title = { Text(stringResource(config_avatar)) },
        subtitle = { Text(stringResource(config_avatar)) },
    )
}

@Composable
fun Avatar(value: String?, modifier: Modifier = Modifier) = Surface(modifier, shapes.small, colors.onSurface, border = BorderStroke(2.dp, colors.primary), elevation = 2.dp) {
    if (value != null) {
        val bytes = decode(value, DEFAULT)
        Image(decodeByteArray(bytes, 0, bytes.size).asImageBitmap(), null, Modifier.s)
    }
}

@Composable
fun PlayerIcon(id: PlayerId, info: PlayerInfo, modifier: Modifier = Modifier) = when {
    info.avatar != null -> Avatar(info.avatar, modifier)
    else -> Icon(if (info.address == null) TwoTone.Android else if (id == 0) TwoTone.Person else TwoTone.Bluetooth, "icon", modifier)
}

@Composable
fun PlayerUi(game: Game, player: Player) = Surface(
    Modifier.slot(
        game
            .slotOf(player.id)
            .position(0, 1, 1), 0f, 0.deg, 0.deg, 1f, 0f, 0f, shapes.small
    ).z.size(64.dp),
    shapes.small,
    colors.surface.copy(medium),
    elevation = 2.dp,
) {
    Column(Modifier.p8, Arrangement.Center, CenterHorizontally) {
        val info = game.infos.elementAt(player.id)
        PlayerIcon(player.id, info, Modifier.s36)
        Text(info.body1, style = typography.caption)
    }
}
