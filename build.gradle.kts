plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.dddheroes"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

ext {
    set("axonVersion", "4.10.3")
    set("assertkVersion", "0.28.1")
}

dependencies {
    val axonVersion: String by project
    val assertkVersion: String by project

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:$assertkVersion")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Axon Framework
    implementation("org.axonframework:axon-spring-boot-starter:$axonVersion")
    implementation("org.axonframework:axon-modelling:$axonVersion")
    testImplementation("org.axonframework:axon-test:$axonVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    useJUnitPlatform()
}