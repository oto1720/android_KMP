package org.example.project

import web.navigator.navigator

class JsPlatform: Platform {
    private val userAgent = navigator.userAgent
    private val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")

    override val name: String = userAgent.findAnyOf(browserList, ignoreCase = true)
            ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
            ?: "Unknown"
}

actual fun getPlatform(): Platform = JsPlatform()

// このプロジェクトには wasmJs に加えて js ターゲットもあるので、こちらにも actual が必要。
actual fun nowFormatted(): String = js("new Date().toTimeString().slice(0,5)")

// Step 2-4: Web(JS ターゲット) も localStorage に保存する。
//   wasmJs と同じ localStorage だが、ターゲットが別なので actual をもう1つ書く。
actual fun saveMemos(data: String) {
    js("localStorage.setItem('memos', data)")
}

actual fun loadMemos(): String = js("localStorage.getItem('memos') || ''")
