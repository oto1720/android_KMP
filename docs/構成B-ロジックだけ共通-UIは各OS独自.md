# 構成B: ロジックだけ共通・UIは各OS独自（variant/native-ui ブランチ）

このブランチ `variant/native-ui` は、メインのハンズオン（`shared` に UI まで含める Compose Multiplatform 構成）とは**別の設計**で同じ「KMPメモ帳」を作ったものです。

- **メイン構成（answer 系ブランチ）**: `shared` にロジック **＋ UI(Compose Multiplatform)** を置き、3OSで同じ `App()` を描画する
- **この構成（variant/native-ui）**: `shared` には **ロジックだけ** を置き、UI は各OSがネイティブに書く

KMP には大きく2つの使い方があり、これは後者（昔からある王道の使い方）を体験するための枝です。

## 何を共通化し、何を各OSで書くか

```
shared/commonMain （共通・UIなし）
  - Memo             データモデル
  - MemoStorage      List<Memo> ⇄ String の変換
  - Platform(expect) nowFormatted / saveMemos / loadMemos の約束
  - MemoLogic        ★アプリのルール（追加/削除/お気に入り/検索並べ替え/保存）

shared/androidMain・iosMain・jsMain・wasmJsMain （expect の実装）
  - 時刻API・保存先（SharedPreferences / NSUserDefaults / localStorage）

androidApp   UI = Jetpack Compose（MainActivity.kt）
iosApp       UI = SwiftUI（ContentView.swift）
webApp       UI = ブラウザの DOM 直接操作（Kotlin/JS, main.kt）
```

ポイントは、**3つの UI がどれも同じ `MemoLogic` を呼んでいる**ことです。画面の見た目と組み立ては各OSでバラバラですが、「メモをどう足す・消す・並べる・保存するか」というルールは1か所（`shared/commonMain/MemoLogic.kt`）だけにあります。

## `MemoLogic` の使い方（3OS共通の呼び出し）

`MemoLogic` は状態を持たない純粋な関数の集まりです。状態（今どんな `memos` か）は各UIが持ち、変換だけを `MemoLogic` に頼みます。

| 操作 | 呼び出し |
|---|---|
| 起動時の復元 | `MemoLogic.restore()` → `List<Memo>` |
| 保存 | `MemoLogic.persist(memos)` |
| 追加 | `MemoLogic.add(memos, text)` → 新しい `List<Memo>` |
| 削除 | `MemoLogic.remove(memos, id)` |
| お気に入り切替 | `MemoLogic.toggleFavorite(memos, id)` |
| 表示用に整形 | `MemoLogic.visible(memos, query)` |

- Android（Kotlin）: `MemoLogic.add(memos, input)`
- iOS（Swift）: `MemoLogic.shared.add(memos: memos, text: input)`
- Web（Kotlin/JS）: `MemoLogic.add(memos, inputField.value)`

同じ関数を、各言語の書き方で呼んでいるだけです。

## 起動コマンド

UI が各OSネイティブになった以外、起動方法はメイン構成とほぼ同じです。ただし **Web は Compose をやめて Kotlin/JS + DOM 構成にしたため、`js` ターゲットで起動**します（メイン構成の `wasmJs...` ではありません）。

```bash
# Web（Kotlin/JS + DOM）
./gradlew :webApp:jsBrowserDevelopmentRun

# Android（エミュレータ／実機を起動してから）
./gradlew :androidApp:installDebug

# iOS（Xcode でシミュレータを選んで Run）
open iosApp/iosApp.xcodeproj
```

## メイン構成との違い（ファイル単位）

| | メイン構成 | この構成 |
|---|---|---|
| `shared` の UI | `App.kt`（Compose） | なし（削除） |
| iOSブリッジ | `MainViewController.kt`（ComposeUIViewController） | なし（削除） |
| ロジック集約 | `App()` 内に直書き | `MemoLogic.kt` に分離 |
| `shared/build.gradle.kts` | Compose プラグイン＋依存あり | Compose なし（純ロジック） |
| Android UI | `shared` の `App()` を呼ぶだけ | `MainActivity` に Compose UI を自前実装 |
| iOS UI | Compose を SwiftUI に貼るだけ | `ContentView.swift` に SwiftUI で自前実装 |
| Web UI | `ComposeViewport { App() }` | `main.kt` で DOM を直接組み立て |

## メリット / デメリット（体験して確認する）

**メリット**
- 各OSで「そのOSらしい」UI・操作感にできる（SwiftUI / Jetpack Compose / HTML）
- 既存のネイティブアプリにロジックだけ後入れしやすい

**デメリット**
- UI を 3 回書く必要がある（この構成では実際に3つ書いた）
- Kotlin の状態を各UIへ渡す部分は自前（今回は「変換関数＋各UIが状態を持つ」形で単純化。実務では `StateFlow` + KMP-NativeCoroutines などを使うことが多い）

## 補足（検証状況）

- `shared`（js / iOS 向け）、`webApp`（Kotlin/JS）、`androidApp`（Compose）の**コンパイルは確認済み**です。
- iOS の `ContentView.swift` は Xcode でのビルドはこの環境では未確認です。Kotlin `object` は Swift から `MemoLogic.shared`、`List<Memo>` は Swift の `[Memo]`、`getPlatform()` は `PlatformKt.getPlatform()` として見える前提で書いています。Xcode で開いてビルドし、必要なら細部を調整してください。
