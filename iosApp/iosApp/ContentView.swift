import SwiftUI
import Shared

// この構成（variant/native-ui）では、iOS の UI は SwiftUI でネイティブに書く。
// 共通なのは shared の MemoLogic（ロジック）だけ。
// Kotlin の object MemoLogic は Swift からは MemoLogic.shared で呼べる。
struct ContentView: View {
    // 状態は UI 側（SwiftUI）が持つ。起動時に shared から復元する。
    @State private var memos: [Memo] = MemoLogic.shared.restore()
    @State private var input: String = ""
    @State private var query: String = ""

    // 表示用リストの組み立て（絞り込み・並べ替え）は shared に任せる。
    private var visibleMemos: [Memo] {
        MemoLogic.shared.visible(memos: memos, query: query)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("KMPメモ帳 (iOS)").font(.title2).bold()
                Spacer()
                Text("\(memos.count)件").font(.caption).foregroundColor(.secondary)
            }

            HStack {
                TextField("いま思ったこと", text: $input)
                    .textFieldStyle(.roundedBorder)
                Button("追加") { add() }
            }

            TextField("🔍 検索", text: $query)
                .textFieldStyle(.roundedBorder)

            if visibleMemos.isEmpty {
                Text(memos.isEmpty ? "まだメモがありません" : "一致するメモがありません")
                    .foregroundColor(.secondary)
            }

            List {
                ForEach(visibleMemos, id: \.id) { memo in
                    HStack {
                        VStack(alignment: .leading) {
                            Text(memo.text)
                            Text(memo.createdAt).font(.caption).foregroundColor(.secondary)
                        }
                        // 本文タップで削除。
                        .contentShape(Rectangle())
                        .onTapGesture { remove(memo) }
                        Spacer()
                        // ☆/★ タップでお気に入り切り替え。
                        Text(memo.favorite ? "★" : "☆")
                            .onTapGesture { toggle(memo) }
                    }
                }
            }

            Text("Running on: \(PlatformKt.getPlatform().name)")
                .font(.caption).foregroundColor(.secondary)
        }
        .padding()
    }

    // 追加・削除・切り替えのたびに shared のロジックを呼び、保存する。
    private func add() {
        memos = MemoLogic.shared.add(memos: memos, text: input)
        input = ""
        MemoLogic.shared.persist(memos: memos)
    }

    private func remove(_ memo: Memo) {
        memos = MemoLogic.shared.remove(memos: memos, id: memo.id)
        MemoLogic.shared.persist(memos: memos)
    }

    private func toggle(_ memo: Memo) {
        memos = MemoLogic.shared.toggleFavorite(memos: memos, id: memo.id)
        MemoLogic.shared.persist(memos: memos)
    }
}
