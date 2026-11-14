plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "com.rafaelbonasio.tabletennisapp.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room3.runtime)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
}
