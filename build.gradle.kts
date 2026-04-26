plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

group = "io.github.klique"
version = "1.0.0"

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
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
