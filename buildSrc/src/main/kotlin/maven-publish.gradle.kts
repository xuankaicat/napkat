package buildsrc.convention

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()
    
    // Coordinates are automatically derived from project.group, project.name, project.version
    // We set them in gradle.properties and build.gradle.kts (group/version)

    pom {
        name.set(project.name)
        description.set("NapCat Bot Kotlin SDK")
        inceptionYear.set("2024")
        
        url.set(providers.gradleProperty("POM_URL"))
        
        licenses {
            license {
                name.set(providers.gradleProperty("POM_LICENCE_NAME"))
                url.set(providers.gradleProperty("POM_LICENCE_URL"))
                distribution.set(providers.gradleProperty("POM_LICENCE_DIST"))
            }
        }
        
        developers {
            developer {
                id.set(providers.gradleProperty("POM_DEVELOPER_ID"))
                name.set(providers.gradleProperty("POM_DEVELOPER_NAME"))
                email.set(providers.gradleProperty("POM_DEVELOPER_EMAIL"))
            }
        }
        
        scm {
            url.set(providers.gradleProperty("POM_SCM_URL"))
            connection.set(providers.gradleProperty("POM_SCM_CONNECTION"))
            developerConnection.set(providers.gradleProperty("POM_SCM_DEV_CONNECTION"))
        }
    }
}
