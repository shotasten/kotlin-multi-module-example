plugins {
    id("java-library")
    id("java-test-fixtures")
    id("maven-publish")
}

dependencies {
    // kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 3rd party
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter")
    implementation("com.mysql:mysql-connector-j")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test")
    testImplementation("io.mockk:mockk")
    testImplementation("com.ninja-squad:springmockk")
    testImplementation("com.ninja-squad:DbSetup-kotlin")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:mysql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    bootJar {
        enabled = false
    }
    jar {
        enabled = true
    }
}
