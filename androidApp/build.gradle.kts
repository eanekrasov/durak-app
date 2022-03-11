plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 26
        targetSdk = 31
    }
    buildFeatures {
        compose = true
    }
    composeOptions.kotlinCompilerExtensionVersion = "1.2.0-alpha05"
}

// region java version

android {
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

// endregion

android {
    defaultConfig {
        applicationId = "durak.app.android"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=kotlin.contracts.ExperimentalContracts",
            "-Xopt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    api(project(":shared"))
    api("androidx.core:core-ktx:1.7.0")
    api("androidx.activity:activity-compose:1.4.0")
    api("com.google.dagger:hilt-android:2.41")
    kapt("com.google.dagger:hilt-compiler:2.41")
    api("com.arkivanov.decompose:decompose:0.5.1")
    api("com.arkivanov.decompose:decompose:0.5.1")
    api("com.arkivanov.decompose:extensions-compose-jetpack:0.5.1")
    api("androidx.compose.runtime:runtime:1.2.0-alpha05")
    api("androidx.compose.foundation:foundation:1.2.0-alpha05")
    api("androidx.compose.material:material:1.2.0-alpha05")
    api("androidx.compose.material:material-icons-extended:1.2.0-alpha05")
    api("androidx.compose.ui:ui:1.2.0-alpha05")
    api("androidx.compose.ui:ui-tooling:1.2.0-alpha05")
    api("androidx.compose.ui:ui-tooling-preview:1.2.0-alpha05")
    api("androidx.datastore:datastore:1.0.0")
    api("com.google.accompanist:accompanist-insets-ui:0.24.3-alpha")
    api("com.google.accompanist:accompanist-swiperefresh:0.24.3-alpha")
    api("com.google.accompanist:accompanist-permissions:0.24.3-alpha")
    api("com.google.accompanist:accompanist-navigation-material:0.24.3-alpha")
    api("com.google.accompanist:accompanist-navigation-animation:0.24.3-alpha")
    api("androidx.navigation:navigation-compose:2.4.1")
    api("androidx.navigation:navigation-fragment-ktx:2.4.1")
    api("androidx.hilt:hilt-navigation-compose:1.0.0")
    api("androidx.hilt:hilt-navigation-fragment:1.0.0")
    api("androidx.lifecycle:lifecycle-common-java8:2.4.1")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    api("com.google.android.material:material:1.5.0")
    api("androidx.appcompat:appcompat:1.4.1")
    api("androidx.preference:preference:1.2.0")
    api("androidx.preference:preference-ktx:1.2.0")
    api("androidx.compose.runtime:runtime-livedata:1.1.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.41")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.0-alpha05")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.2.0-alpha05")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.4.1")
    androidTestImplementation("com.google.dagger:hilt-android:2.41")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.41")
}
