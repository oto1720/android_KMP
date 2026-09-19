package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// この構成（variant/native-ui）では、UI は Android ネイティブ（Jetpack Compose）で書く。
// 共通なのは shared の MemoLogic（ロジック）だけ。画面の組み立ては全部この androidApp 側。
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // SharedPreferences 用に Context を shared 側へ渡す（Android だけ必要な一手間）。
        androidContext = applicationContext

        setContent {
            MaterialTheme {
                MemoScreen()
            }
        }
    }
}

@Composable
private fun MemoScreen() {
    // 状態は UI 側で持つ。起動時に shared の MemoLogic.restore() で復元する。
    var memos by remember { mutableStateOf(MemoLogic.restore()) }
    var input by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }

    // memos が変わるたびに shared 経由で保存する。
    LaunchedEffect(memos) {
        MemoLogic.persist(memos)
    }

    // 表示用リストの組み立て（絞り込み・並べ替え）は shared に任せる。
    val visibleMemos = MemoLogic.visible(memos, query)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("KMPメモ帳 (Android)", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f))
            Text("${memos.size}件", style = MaterialTheme.typography.labelMedium)
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("いま思ったこと") },
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    memos = MemoLogic.add(memos, input)
                    input = ""
                }
            ) {
                Text("追加")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("🔍 検索") },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        if (visibleMemos.isEmpty()) {
            Text(
                if (memos.isEmpty()) "まだメモがありません" else "一致するメモがありません",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(visibleMemos, key = { it.id }) { memo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { memos = MemoLogic.remove(memos, memo.id) }
                        ) {
                            Text(memo.text)
                            Text(memo.createdAt, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(
                            text = if (memo.favorite) "★" else "☆",
                            modifier = Modifier
                                .clickable { memos = MemoLogic.toggleFavorite(memos, memo.id) }
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Text(
            "Running on: ${getPlatform().name}",
            style = MaterialTheme.typography.labelSmall
        )
    }
}
