package org.example.project

// Step 2-3: メモ一覧 ⇄ 文字列 の変換を「共通コード」に集約する。
//
// 保存 API（SharedPreferences / NSUserDefaults / localStorage）はどれも
// 「1本の文字列」しか扱えない。だから List<Memo> と String を相互変換する処理は
// 各 OS で書き分けず、ここ commonMain に 1 回だけ書く。
// → 書き分ける（expect/actual にする）のは「保存先」だけに最小化できる。
//
// ※ 依存ゼロの手書き実装。実務では kotlinx.serialization を使うのが普通です。

// 1メモ = 1行。フィールドはタブ区切りで並べる。
private const val FIELD_SEP = "\t"
private const val LINE_SEP = "\n"

// text にタブや改行が混ざっても壊れないよう、保存前にエスケープする。
private fun escape(value: String): String =
    value.replace("\\", "\\\\")
        .replace("\t", "\\t")
        .replace("\n", "\\n")

// エスケープを元に戻す。
private fun unescape(value: String): String {
    val sb = StringBuilder()
    var i = 0
    while (i < value.length) {
        val c = value[i]
        if (c == '\\' && i + 1 < value.length) {
            when (value[i + 1]) {
                't' -> sb.append('\t')
                'n' -> sb.append('\n')
                '\\' -> sb.append('\\')
                else -> sb.append(value[i + 1])
            }
            i += 2
        } else {
            sb.append(c)
            i++
        }
    }
    return sb.toString()
}

// List<Memo> → String（保存用に文字列化する）
fun encodeMemos(memos: List<Memo>): String =
    memos.joinToString(LINE_SEP) { memo ->
        listOf(
            memo.id.toString(),
            memo.favorite.toString(),
            escape(memo.createdAt),
            escape(memo.text),
        ).joinToString(FIELD_SEP)
    }

// String → List<Memo>（読み出した文字列をメモ一覧に戻す）
fun decodeMemos(data: String): List<Memo> {
    if (data.isBlank()) return emptyList()
    return data.split(LINE_SEP).mapNotNull { line ->
        val parts = line.split(FIELD_SEP)
        // 壊れた行（項目が足りない・id が数値でない）はスキップする。
        if (parts.size < 4) return@mapNotNull null
        Memo(
            id = parts[0].toLongOrNull() ?: return@mapNotNull null,
            favorite = parts[1].toBoolean(),
            createdAt = unescape(parts[2]),
            text = unescape(parts[3]),
        )
    }
}
