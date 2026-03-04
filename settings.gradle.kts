dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":napkat-core")
include(":napkat-spring-boot-starter")
include(":napkat-spring-boot-starter-test")

rootProject.name = "napkat"