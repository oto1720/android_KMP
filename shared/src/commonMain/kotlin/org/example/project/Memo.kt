package org.example.project

// Step 1-1: データモデル。
// Kotlin の data class がそのまま全プラットフォーム（iOS 含む）で動く。
data class Memo(
    val id: Long,
    val text: String,
    val createdAt: String,
)
