package org.example.project

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

// JS の Date をそのまま利用（Wasm から JS を呼ぶ）。
actual fun nowFormatted(): String = jsNowString()

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun jsNowString(): String = js("new Date().toTimeString().slice(0,5)")

// Step 2-4: Web はブラウザの localStorage にそのまま保存できる（受け皿は不要）。
//   Wasm からブラウザの localStorage API を呼び出している。
actual fun saveMemos(data: String) = jsSave(data)

actual fun loadMemos(): String = jsLoad()

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun jsSave(data: String): Unit = js("localStorage.setItem('memos', data)")

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun jsLoad(): String = js("localStorage.getItem('memos') || ''")
