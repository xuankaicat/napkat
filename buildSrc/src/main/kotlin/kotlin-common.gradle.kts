package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent
import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()
    @Suppress("UnstableApiUsage")
    androidLibrary {
        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(
                        JvmTarget.JVM_11
                    )
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
//    js()
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs()

    sourceSets {
        commonMain {
            kotlin.srcDirs("src/main/kotlin")
            resources.srcDirs("src/main/resources")
        }
        commonTest {
            kotlin.srcDirs("src/test/kotlin")
            resources.srcDirs("src/test/resources")
        }
    }
}

tasks.withType<Test>().configureEach {
    // Log information about all test results, not only the failed ones.
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}
