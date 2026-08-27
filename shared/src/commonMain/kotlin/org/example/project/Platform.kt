package org.example.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// Step 2-2: 現在時刻を "HH:mm" で返す。共通化できない（各プラットフォームの時刻APIを使う）ため expect にする。
expect fun nowFormatted(): String