plugins {
    id("com.android.library") apply false
    id("com.android.application") apply false
    kotlin("kapt") apply false
    kotlin("android") apply false
    kotlin("multiplatform") apply false
    kotlin("plugin.parcelize") apply false
    kotlin("plugin.serialization") apply false
    id("com.google.dagger.hilt.android") apply false
}

subprojects {
    afterEvaluate {
        project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.apply {
            sourceSets.removeAll {
                setOf("androidAndroidTestRelease", "androidTestFixtures", "androidTestFixturesDebug", "androidTestFixturesRelease").contains(it.name)
            }
        }
    }
}
