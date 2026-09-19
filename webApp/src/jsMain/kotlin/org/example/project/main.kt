package org.example.project

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

// この構成（variant/native-ui）では、Web の UI はブラウザの DOM を直接組み立てる（Compose なし）。
// 共通なのは shared の MemoLogic（ロジック）だけ。
//   状態（memos / query）はこの UI 側が持ち、変化のたびに DOM を描き直す。

private var memos: List<Memo> = MemoLogic.restore()
private var query: String = ""

private lateinit var countLabel: HTMLElement
private lateinit var listContainer: HTMLElement
private lateinit var inputField: HTMLInputElement

fun main() {
    val app = document.getElementById("app") as? HTMLElement ?: return
    app.setAttribute(
        "style",
        "max-width:600px;margin:0 auto;padding:16px;font-family:sans-serif;",
    )

    // タイトル + 件数
    val header = div("display:flex;align-items:center;justify-content:space-between;")
    val title = document.createElement("h2") as HTMLElement
    title.textContent = "KMPメモ帳 (Web)"
    countLabel = document.createElement("span") as HTMLElement
    countLabel.setAttribute("style", "color:#666;font-size:14px;")
    header.appendChild(title)
    header.appendChild(countLabel)
    app.appendChild(header)

    // 入力 + 追加ボタン
    val inputRow = div("display:flex;gap:8px;margin:12px 0;")
    inputField = document.createElement("input") as HTMLInputElement
    inputField.placeholder = "いま思ったこと"
    inputField.setAttribute("style", "flex:1;padding:8px;")
    val addButton = document.createElement("button") as HTMLElement
    addButton.textContent = "追加"
    addButton.setAttribute("style", "padding:8px 16px;")
    addButton.onclick = {
        memos = MemoLogic.add(memos, inputField.value)
        inputField.value = ""
        persistAndRender()
    }
    inputRow.appendChild(inputField)
    inputRow.appendChild(addButton)
    app.appendChild(inputRow)

    // 検索
    val searchField = document.createElement("input") as HTMLInputElement
    searchField.placeholder = "🔍 検索"
    searchField.setAttribute("style", "width:100%;padding:8px;margin-bottom:12px;box-sizing:border-box;")
    searchField.oninput = {
        query = searchField.value
        render()
    }
    app.appendChild(searchField)

    // 一覧
    listContainer = div("")
    app.appendChild(listContainer)

    // どの環境で動いているか（getPlatform() も shared の共通API）
    val platform = document.createElement("p") as HTMLElement
    platform.setAttribute("style", "color:#888;font-size:12px;")
    platform.textContent = "Running on: ${getPlatform().name}"
    app.appendChild(platform)

    render()
}

private fun persistAndRender() {
    MemoLogic.persist(memos)
    render()
}

private fun render() {
    countLabel.textContent = "${memos.size}件"
    listContainer.innerHTML = ""

    val visible = MemoLogic.visible(memos, query)
    if (visible.isEmpty()) {
        val empty = document.createElement("p") as HTMLElement
        empty.textContent = if (memos.isEmpty()) "まだメモがありません" else "一致するメモがありません"
        empty.setAttribute("style", "color:#888;")
        listContainer.appendChild(empty)
        return
    }

    visible.forEach { memo ->
        val card = div(
            "border:1px solid #ddd;border-radius:8px;padding:12px;margin:8px 0;" +
                "display:flex;align-items:center;justify-content:space-between;",
        )

        // 本文タップで削除。
        val textBox = div("cursor:pointer;flex:1;")
        val textLine = div("")
        textLine.textContent = memo.text
        val timeLine = div("color:#888;font-size:12px;")
        timeLine.textContent = memo.createdAt
        textBox.appendChild(textLine)
        textBox.appendChild(timeLine)
        textBox.onclick = {
            memos = MemoLogic.remove(memos, memo.id)
            persistAndRender()
        }

        // ☆/★ タップでお気に入り切り替え。
        val star = document.createElement("span") as HTMLElement
        star.textContent = if (memo.favorite) "★" else "☆"
        star.setAttribute("style", "cursor:pointer;font-size:20px;padding-left:12px;")
        star.onclick = {
            memos = MemoLogic.toggleFavorite(memos, memo.id)
            persistAndRender()
        }

        card.appendChild(textBox)
        card.appendChild(star)
        listContainer.appendChild(card)
    }
}

// div を作って style を付けるだけの小さなヘルパー。
private fun div(style: String): HTMLElement {
    val el = document.createElement("div") as HTMLElement
    if (style.isNotEmpty()) el.setAttribute("style", style)
    return el
}
