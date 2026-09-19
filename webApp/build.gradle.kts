plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    // この構成では Web の UI を Compose ではなくブラウザの DOM で直接書く。
    // 素直な Kotlin/JS + DOM 構成にするため js ターゲットのみにする。
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":shared"))
        }
    }
}
