package org.example.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// Step 2-2: 現在時刻を "HH:mm" で返す。共通化できない（各プラットフォームの時刻APIを使う）ため expect にする。
expect fun nowFormatted(): String

// Step 2-3: メモの「保存先」は OS ごとに違う
//   Android=SharedPreferences / iOS=NSUserDefaults / Web=localStorage
// ため、これも expect で「各OSが実装する」と約束だけしておく。
// 中身（List<Memo> ⇄ String の変換）は MemoStorage.kt に共通で書き、
// ここでは「1本の文字列」を保存・読み出しするだけの最小インターフェースにする。
expect fun saveMemos(data: String)
expect fun loadMemos(): String
