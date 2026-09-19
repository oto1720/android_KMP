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
        // Step 1-4: 検索キーワード。
        var query by remember { mutableStateOf("") }

        // Step 2-5: 起動時に保存済みのメモを読み込む（永続化）。
        //   loadMemos() で保存済みの文字列を取り出し、decodeMemos() で List<Memo> に戻す。
        var memos by remember { mutableStateOf(decodeMemos(loadMemos())) }

        // nextId は memos.size ではなく別で持つ（削除後に id が重複するのを防ぐ）。
        //   Step 2-5: 復元したメモの「最大 id + 1」から採番する（再起動後の id 衝突を防ぐ）。
        var nextId by remember { mutableStateOf((memos.maxOfOrNull { it.id } ?: -1L) + 1L) }

        // Step 2-5: memos が変わるたびに自動保存する。
        //   保存処理を 1 箇所にまとめることで「保存し忘れ」が起きない。
        LaunchedEffect(memos) {
            saveMemos(encodeMemos(memos))
        }

        // Step 1-4 / 1-5: 画面に表示するメモ一覧を作る。
        //   元データ(memos)はそのまま持ち、表示用(visibleMemos)を別に組み立てる。
        //   1) 検索キーワードで絞り込み（filter）
        //   2) 「お気に入り優先 → 新しい順」で並べ替え（sortedWith）
        //   ↑ これらは全部ただの Kotlin なので、書き分けゼロで 3 OS すべてに効く。
        val visibleMemos = memos
            .filter { query.isBlank() || it.text.contains(query, ignoreCase = true) }
            .sortedWith(
                compareByDescending<Memo> { it.favorite }
                    .thenByDescending { it.id }
            )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp)
        ) {
            // Step 1-5: タイトルの右に件数を出す。
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("KMPメモ帳", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.weight(1f))
                Text("${memos.size}件", style = MaterialTheme.typography.labelMedium)
            }

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
                            // 新しいメモを末尾に足した「新しいリスト」を代入 → Compose が再描画。
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

            Spacer(Modifier.height(8.dp))

            // Step 1-4: 検索欄。ここに打つと visibleMemos が絞り込まれる。
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("🔍 検索") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Step 1-3: メモ一覧（本文タップで削除、★タップでお気に入り切り替え）
            if (visibleMemos.isEmpty()) {
                Text(
                    // メモ自体が無いのか、検索でヒットしないのかを出し分ける。
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
                            // 本文部分をタップすると、そのメモを除いた新リストを代入＝削除。
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { memos = memos - memo }
                            ) {
                                Text(memo.text)
                                Text(memo.createdAt, style = MaterialTheme.typography.labelSmall)
                            }
                            // Step 1-5: ★をタップすると、その 1 件だけ favorite を反転した新リストを作る。
                            //   copy で 1 件だけ更新し、それ以外はそのまま map で通す。
                            Text(
                                text = if (memo.favorite) "★" else "☆",
                                modifier = Modifier
                                    .clickable {
                                        memos = memos.map {
                                            if (it.id == memo.id) it.copy(favorite = !it.favorite)
                                            else it
                                        }
                                    }
                                    .padding(start = 8.dp)
                            )
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
