plugins {
    kotlin("jvm") version "2.3.21"
}

group = "de.einnik.yamler"
version = "3.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.6")
}

kotlin {
    jvmToolchain(25)
}