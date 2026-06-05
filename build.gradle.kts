plugins {
    java
    `maven-publish`
}

allprojects {
    group = "de.einnik"
    version = "3.0.3-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        withSourcesJar()
        withJavadocJar()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-path"))
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            name = "papermc-repo"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }

        maven {
            name = "Sonatype-Snapshots"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("org.projectlombok:lombok:1.18.44")
        annotationProcessor("org.projectlombok:lombok:1.18.44")
    }

    tasks.test {
        useTestNG()
    }

    tasks.processResources {
        val buildNumber = System.getenv("BUILD_NUMBER") ?: "0"

        inputs.property("version", project.version)
        inputs.property("name", project.name)
        inputs.property("buildNumber", buildNumber)

        filesMatching(listOf("**/*.yml", "**/*.properties")) {
            expand(
                "version" to project.version,
                "project.version" to project.version,
                "project.name" to project.name,
                "build.number" to buildNumber,
                "build" to mapOf(
                    "number" to buildNumber
                ),
                "project" to mapOf(
                    "version" to project.version,
                    "name" to project.name
                )
            )
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = when (project.name) {
                    "core" -> "Yamler-Core"
                    "paper" -> "Yamler-Paper"
                    else -> project.name
                }
            }
        }
    }
}