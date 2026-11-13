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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":data"))

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.foundation.layout)
            implementation(libs.androidx.compose.material.icons.core)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.text)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.foundation)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.material3)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.ui)
            implementation(libs.compose.components.resources)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.haze)
            implementation(libs.haze.blur)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.androidx.room3.runtime)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
        }
    }
}

compose.resources {
    packageOfResClass = "com.rafaelbonasio.tabletennisapp"
}