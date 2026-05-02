plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
    id("com.gradleup.nmcp") version "1.4.4"
    id("com.gradleup.shadow") version "9.4.1"
    id("org.graalvm.buildtools.native") version "1.1.0"
}

group = "io.github.bryansant"
version = System.getenv("GITHUB_REF_NAME")?.let { ref ->
    if (ref.startsWith("v")) ref.removePrefix("v")
    else ref
} ?: "1.0.7-SNAPSHOT"

kotlin {
    jvmToolchain(25)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("k.jar")
    manifest {
        attributes("Main-Class" to "io.github.bryansant.klique.MainKt")
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("k")
            mainClass.set("io.github.bryansant.klique.MainKt")
            buildArgs.addAll(
                "-H:+UnlockExperimentalVMOptions",
                "--no-fallback",
                "-Os",
                "-H:+OmitInlinedMethodDebugLineInfo",
                "-H:-StackTrace",
                "--gc=epsilon"
            )
        }
    }
}

tasks.register<Exec>("compressBinary") {
    val binaryPath = layout.buildDirectory.file("native/nativeCompile/k").get().asFile.absolutePath
    commandLine("upx", "--best", "--lzma", binaryPath)
    isIgnoreExitValue = true
}

tasks.named("nativeCompile") {
    finalizedBy("compressBinary")
}

dependencies {
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
                description.set("A pure-Kotlin terminal UI library with idiomatic DSL components")
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
        publishingType = "AUTOMATIC"
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
