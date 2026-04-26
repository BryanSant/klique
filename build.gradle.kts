plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
}

group = "io.github.klique"
version = System.getenv("GITHUB_REF_NAME")?.removePrefix("v") ?: "1.0.0"

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
                url.set("https://github.com/klique/klique")
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
                    connection.set("scm:git:git://github.com/klique/klique.git")
                    developerConnection.set("scm:git:ssh://github.com/klique/klique.git")
                    url.set("https://github.com/klique/klique")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
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

signing {
    val key = System.getenv("GPG_PRIVATE_KEY")
    val password = System.getenv("GPG_PASSPHRASE")
    if (key != null && password != null) {
        useInMemoryPgpKeys(key, password)
        sign(publishing.publications["maven"])
    }
}
