plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 26
        targetSdk = 31
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    sourceSets {
        configureEach {
            setRoot("src/android${name.capitalize()}")
            java.srcDirs("src/android${name.capitalize()}/kotlin")
            res.srcDirs("src/android${name.capitalize()}/res", "src/common${name.capitalize()}/res")
        }
    }
    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    }
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(iosArm64(), iosSimulatorArm64(), iosX64()).forEach {
        it.binaries.framework {
            baseName = "shared"
            transitiveExport = true
            export("com.arkivanov.decompose:decompose:0.5.1")
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.contracts.ExperimentalContracts")
            }
        }

        val commonMain by getting
        val commonTest by getting
        val androidMain by getting { dependsOn(commonMain) }
        val androidTest by getting { dependsOn(commonTest) }
        val iosMain by creating { dependsOn(commonMain) }
        val iosTest by creating { dependsOn(commonTest) }
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosX64Test by getting { dependsOn(iosTest) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosArm64Test by getting { dependsOn(iosTest) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }

        commonMain.dependencies {
            api("com.arkivanov.decompose:decompose:0.5.1")
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt") { version { require("1.6.0-native-mt") } }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-annotations-common"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0-native-mt") { version { require("1.6.0-native-mt") } }
        }
        androidTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}
