package com.company.velogappshorcut

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.company.velogappshorcut.ui.theme.VelogAppShorcutTheme

class MainActivity : ComponentActivity() {

    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            VelogAppShorcutTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp , Alignment.CenterVertically
                    )
                ) {
                    when(viewModel.shortcutType) {
                        ShortcutType.STATIC -> Text("정적 바로가기 누름")
                        ShortcutType.DYNAMIC -> Text("동적 바로가기 누름")
                        ShortcutType.PINNED -> Text("고정된 바로가기 누름")
                        null -> Unit
                    }
                    Button(
                        onClick = ::addDynamicShortcut
                    ) {
                        Text("동적 바로가기 추가하기")
                    }
                    Button(
                        onClick = ::addPinnedShortcut
                    ) {
                        Text("고정된 바로가기 추가하기")
                    }
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun addPinnedShortcut() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val shortcutManager = getSystemService<ShortcutManager>()!!

        val shortcut = ShortcutInfo.Builder(applicationContext , "pinned")
            .setShortLabel("고정된 짧은 라벨")
            .setLongLabel("고정된 긴 라벨")
            .setIcon(Icon.createWithResource(
                applicationContext , R.drawable.ic_launcher_background
            ))
            .setIntent(
                Intent(applicationContext, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_id" , "pinned")
                }
            )
            .build()
        val callbackIntent = shortcutManager.createShortcutResultIntent(shortcut)
        val successPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            callbackIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        shortcutManager.requestPinShortcut(shortcut ,successPendingIntent.intentSender)
    }

    private fun addDynamicShortcut() {
        val shortcut = ShortcutInfoCompat.Builder(applicationContext,"dynamic")
            .setShortLabel("동적인 짧은 라벨")
            .setLongLabel("동적인 긴 라벨")
            .setIcon(IconCompat.createWithResource(
                applicationContext, R.drawable.ic_launcher_background
            ))
            .setIntent(
                Intent(applicationContext, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_id" , "dynamic")
                }
            )
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(applicationContext , shortcut)
    }

    private fun handleIntent(intent : Intent?) {
        intent?.let {
            when(intent.getStringExtra("shortcut_id")) {
                "static" -> viewModel.onShortcutClicked(ShortcutType.STATIC)
                "dynamic" -> viewModel.onShortcutClicked(ShortcutType.DYNAMIC)
                "pinned" -> viewModel.onShortcutClicked(ShortcutType.PINNED)
            }
        }
    }

}