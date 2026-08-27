package org.example.project

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Step 1-2: 画面の状態。remember + mutableStateOf は Flutter の setState に近い。
        var input by remember { mutableStateOf("") }
        var memos by remember { mutableStateOf(listOf<Memo>()) }
        // nextId は memos.size ではなく別で持つ（削除後に id が重複するのを防ぐ）。
        var nextId by remember { mutableStateOf(0L) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp)
        ) {
            Text("ひとことメモ", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            // Step 1-2: 入力欄と追加ボタン
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
                        if (input.isNotBlank()) {
                            memos = memos + Memo(
                                id = nextId,
                                text = input.trim(),
                                createdAt = nowFormatted()
                            )
                            nextId++
                            input = ""
                        }
                    }
                ) {
                    Text("追加")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Step 1-3: メモ一覧（カードをタップで削除）
            if (memos.isEmpty()) {
                Text("まだメモがありません", style = MaterialTheme.typography.bodyMedium)
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(memos, key = { it.id }) { memo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { memos = memos - memo }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(memo.text)
                            Text(memo.createdAt, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Step 2-1: いまどの環境で動いているか（既存の getPlatform() を再利用）
            Text(
                "Running on: ${getPlatform().name}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
