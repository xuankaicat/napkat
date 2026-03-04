plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.maven-publish")
}

dependencies {
    api(project(":napkat-core"))
    api(project(":napkat-spring-boot-starter"))
    
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.mock)
    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)
    
    // Test dependencies
    testImplementation(kotlin("test"))
}
