package org.example.project

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

// Kotlin から iOS の Foundation API（NSDateFormatter）を直接呼べる。
actual fun nowFormatted(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "HH:mm"
    return formatter.stringFromDate(NSDate())
}

// Step 2-4: iOS は NSUserDefaults にそのまま保存できる（受け皿は不要）。
//   ここでも Kotlin から Foundation の API を直接叩いている。
private const val KEY = "memos"

actual fun saveMemos(data: String) {
    NSUserDefaults.standardUserDefaults.setObject(data, KEY)
}

actual fun loadMemos(): String =
    NSUserDefaults.standardUserDefaults.stringForKey(KEY) ?: ""
