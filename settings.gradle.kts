pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }

    plugins {
        id("com.android.library") version "7.1.2"
        id("com.android.application") version "7.1.2"
        kotlin("kapt") version "1.6.10"
        kotlin("android") version "1.6.10"
        kotlin("multiplatform") version "1.6.10"
        kotlin("plugin.parcelize") version "1.6.10"
        kotlin("plugin.serialization") version "1.6.10"
        id("com.google.dagger.hilt.android") version "2.41"
        id("org.jetbrains.compose") version "1.0.1"
        id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

rootProject.name = "durak"

include(":androidApp", ":shared")
