plugins {
    kotlin("jvm") version "1.9.23"
}

group = "pl.nakodach"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
}

tasks.test {
    useJUnitPlatform()
}