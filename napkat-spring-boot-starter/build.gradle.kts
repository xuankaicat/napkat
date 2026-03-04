plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.maven-publish")
}

dependencies {
    api(project(":napkat-core"))
    api(libs.ktor.client.core)
    api(libs.ktor.client.websockets)


    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)
    implementation(libs.springBoot.starter)
    implementation(libs.springBoot.autoconfigure)
    implementation(kotlin("reflect"))
    
    testImplementation(kotlin("test"))
    testImplementation(libs.springBoot.starter.test)
    testImplementation(project(":napkat-spring-boot-starter-test"))
}
