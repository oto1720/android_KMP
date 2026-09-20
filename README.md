This is a Kotlin Multiplatform project targeting Android, iOS, Web.

---

## 🌿 ブランチ構成（ハンズオン用）

このリポジトリは、KMP講座で使いやすいように「作業用ブランチ」と「答えブランチ」を分けています。
詳しい運用ルールと Step ごとの実装手順は、`handson/start` ブランチの `docs/ブランチ運用.md` / `docs/ハンズオン手順書.md` にまとまっています。

| ブランチ | 用途 |
| --- | --- |
| `handson/start` | **受講者が最初に使う**開始ブランチ。最小限の画面だけが入っており、Step 1-1 から順に実装していく |
| `answer/step-1-1-memo` 〜 `answer/step-2-5-autosave` | 各 Step の**完了状態（答え合わせ用）**。詰まったときに該当 Step を見る |
| `answer/final` | KMPメモ帳の**完成版**（デモ・答え合わせ・復旧用） |

```bash
# 最初に開始ブランチへ
git switch handson/start

# 特定 Step の答えを見る（例: Step 1-4）
git switch answer/step-1-4-search

# 完成版を見る
git switch answer/final
```

作業途中で答えブランチへ移動できない場合は、いったん退避します（`-u` で未追跡ファイルも退避）。

```bash
git stash -u
git switch answer/step-1-4-search
# 戻るとき
git switch handson/start
git stash pop
```

> ⚠️ `git stash -u` や `git clean` は未追跡ファイル（コミットしていない新規ファイル）も退避・削除します。
> 残したいメモや解説ファイルは、退避前にコミットするか別の場所に置いてください。

---

## 🛠 ビルド & 実行（クイックスタート）

前提: JDK 17+ / Android Studio（or IntelliJ）/ iOS は Xcode が必要。すべて `./gradlew`（Windows は `gradlew.bat`）から実行します。

```bash
# 依存の取得とビルド確認（全ターゲットのコンパイル）
./gradlew build

# ── Android ───────────────────────────────
./gradlew :androidApp:assembleDebug          # APK をビルド
./gradlew :androidApp:installDebug           # 接続中の端末/エミュレータへインストール

# ── Web ───────────────────────────────────
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # Wasm 版（高速・モダンブラウザ）
./gradlew :webApp:jsBrowserDevelopmentRun       # JS 版（低速・古いブラウザ対応）
#   → 起動後ブラウザで http://localhost:8080 が開く

# ── iOS ───────────────────────────────────
#   iosApp/ ディレクトリを Xcode で開き、Run（▶）で実行
open iosApp/iosApp.xcodeproj

# ── テスト ─────────────────────────────────
./gradlew :shared:testAndroidHostTest        # Android（ホスト）テスト
./gradlew :shared:wasmJsTest                  # Wasm テスト
./gradlew :shared:jsTest                      # JS テスト
./gradlew :shared:iosSimulatorArm64Test       # iOS シミュレータテスト

# 困ったとき
./gradlew clean                               # ビルド成果物を掃除
./gradlew tasks                               # 実行可能なタスク一覧
```

> IDE から動かす場合は、ツールバーの Run ウィジェットにある実行構成をそのまま使えます。

---

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- Web tests:
  - Wasm target: `./gradlew :shared:wasmJsTest`
  - JS target: `./gradlew :shared:jsTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).