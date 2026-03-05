plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(24)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.vanniktech.mavenPublish.plugin)
}
