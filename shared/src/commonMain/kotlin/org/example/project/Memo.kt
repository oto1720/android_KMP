package org.example.project

// Step 1-1: データモデル。
// Kotlin の data class がそのまま全プラットフォーム（iOS 含む）で動く。
data class Memo(
    val id: Long,
    val text: String,
    val createdAt: String,
    // Step 1-5: お気に入りフラグを後から追加。
    // data class はデフォルト値付きで項目を足せば、既存のコードを壊さずに拡張できる。
    val favorite: Boolean = false,
)
