plugins {
    java
    `maven-publish`
}

allprojects {
    group = "net.cubespace"
    version = "2.4.0-SNAPSHOT"

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

    dependencies {
        testImplementation("org.testng:testng:6.8.7")
    }

    tasks.test {
        useTestNG()
    }

    tasks.processResources {
        inputs.property("version", project.version)
        filesMatching(listOf("**/*.yml", "**/*.properties")) {
            expand("version" to project.version)
        }
    }
}