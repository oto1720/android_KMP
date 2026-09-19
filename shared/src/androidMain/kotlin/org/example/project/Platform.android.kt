package org.example.project

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// SimpleDateFormat を使う（java.time は API 26 以上。minSdk 24 でも動くようにするため）。
actual fun nowFormatted(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

// Step 2-4: ★ここが KMP のリアル：Android だけ「受け皿」が必要。
//   SharedPreferences には Context が必須だが、Context は共通コードからは触れない。
//   そこでアプリ側（MainActivity）から起動時に Context を渡してもらう。
//   iOS / Web にはこの一手間が要らない = この非対称さが KMP 導入の現実的コスト。
lateinit var androidContext: Context

private const val PREF_NAME = "memo_store"
private const val PREF_KEY = "memos"

private val prefs
    get() = androidContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

actual fun saveMemos(data: String) {
    prefs.edit().putString(PREF_KEY, data).apply()
}

actual fun loadMemos(): String =
    prefs.getString(PREF_KEY, "") ?: ""
