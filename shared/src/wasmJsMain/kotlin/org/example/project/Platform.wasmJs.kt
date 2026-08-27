package org.example.project

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

// JS の Date をそのまま利用（Wasm から JS を呼ぶ）。
actual fun nowFormatted(): String = jsNowString()

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun jsNowString(): String = js("new Date().toTimeString().slice(0,5)")