import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    android {
        namespace = "com.rafaelbonasio.tabletennisapp.sharedui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = 24

        androidResources {
            enable = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":data"))

            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.compose.components.resources)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.haze)
            implementation(libs.haze.blur)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.room3.runtime)
        }

        webMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
            implementation(libs.androidx.sqlite.web)
        }
    }
}

compose.resources {
    packageOfResClass = "com.rafaelbonasio.tabletennisapp"
}