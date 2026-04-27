plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
    id("com.gradleup.nmcp") version "1.4.4"
}

group = "io.github.bryansant"
version = System.getenv("GITHUB_REF_NAME")?.let { ref ->
    if (ref.startsWith("v")) ref.removePrefix("v")
    else if (ref == "main") "1.0.4-SNAPSHOT"
    else ref
} ?: "1.0.4-SNAPSHOT"

kotlin {
    jvmToolchain(25)
}

dependencies {
    api(libs.clique.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("klique")
                description.set("A Kotlin DSL wrapper for Clique")
                url.set("https://github.com/BryanSant/klique")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("bsant")
                        name.set("Bryan Sant")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/BryanSant/klique.git")
                    developerConnection.set("scm:git:ssh://github.com/BryanSant/klique.git")
                    url.set("https://github.com/BryanSant/klique")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("GITHUB_REPOSITORY")}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username = System.getenv("CENTRAL_PORTAL_USERNAME") ?: ""
        password = System.getenv("CENTRAL_PORTAL_PASSWORD") ?: ""
        //publishingType = "AUTOMATIC"
        publishingType = "USER_MANAGED"
    }
}

signing {
    val key = System.getenv("GPG_PRIVATE_KEY")
    val password = System.getenv("GPG_PASSPHRASE")
    if (key != null && password != null) {
        useInMemoryPgpKeys(key, password)
        sign(publishing.publications["maven"])
    }
}
