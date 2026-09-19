package org.example.project

// ロジックだけを共通化した「頭脳」。
//
// この構成（variant/native-ui）では、UI は各プラットフォームがネイティブに書く。
//   Android : Jetpack Compose
//   iOS     : SwiftUI
//   Web     : DOM 直接操作（Kotlin/JS）
// そのため commonMain には Compose を一切入れず、
// 「メモをどう足す・消す・並べる・保存する」というアプリのルールだけを置く。
//
// 各 UI はこの MemoLogic を呼び出し、返ってきた List<Memo> を自分の流儀で描画する。
// 状態（今どんな memos か）は各 UI 側が持ち、ここは純粋な変換関数として振る舞う。
object MemoLogic {

    // 起動時: 保存済みの文字列を読み出して List<Memo> に戻す。
    //   loadMemos() は各OSの actual（SharedPreferences / NSUserDefaults / localStorage）。
    fun restore(): List<Memo> = decodeMemos(loadMemos())

    // memos が変わったら呼ぶ: List<Memo> を文字列化して各OSの保存先へ書き込む。
    fun persist(memos: List<Memo>) = saveMemos(encodeMemos(memos))

    // 次に採番する id（削除しても衝突しないよう「最大 id + 1」）。
    fun nextId(memos: List<Memo>): Long = (memos.maxOfOrNull { it.id } ?: -1L) + 1L

    // メモを 1 件追加した新しいリストを返す（空白だけなら何もしない）。
    //   createdAt は各OSの nowFormatted()（時刻APIの actual）で埋める。
    fun add(memos: List<Memo>, text: String): List<Memo> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return memos
        return memos + Memo(
            id = nextId(memos),
            text = trimmed,
            createdAt = nowFormatted(),
        )
    }

    // 指定 id のメモを除いた新しいリストを返す。
    fun remove(memos: List<Memo>, id: Long): List<Memo> =
        memos.filterNot { it.id == id }

    // 指定 id のお気に入りを反転した新しいリストを返す。
    fun toggleFavorite(memos: List<Memo>, id: Long): List<Memo> =
        memos.map { if (it.id == id) it.copy(favorite = !it.favorite) else it }

    // 表示用リスト: 検索で絞り込み → お気に入り優先・新しい順で並べ替え。
    fun visible(memos: List<Memo>, query: String): List<Memo> =
        memos
            .filter { query.isBlank() || it.text.contains(query, ignoreCase = true) }
            .sortedWith(
                compareByDescending<Memo> { it.favorite }
                    .thenByDescending { it.id }
            )
}
