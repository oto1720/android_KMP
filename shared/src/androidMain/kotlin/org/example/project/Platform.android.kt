package org.example.project

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