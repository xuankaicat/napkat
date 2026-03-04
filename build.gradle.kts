plugins {

}

allprojects {
    group = property("GROUP").toString()
    version = property("VERSION_NAME").toString()

    repositories {
        mavenCentral()
        google()
    }
}
