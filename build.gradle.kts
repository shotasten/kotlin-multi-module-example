plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
    kotlin("plugin.noarg") version "2.0.20"
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0" apply false
}

allprojects {
    group = "com.shotaste"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    dependencyManagement {
        dependencies {
            // spring
            dependencySet("org.mybatis.spring.boot:3.0.3") {
                entry("mybatis-spring-boot-starter")
                entry("mybatis-spring-boot-starter-test")
            }

            // 3rd party
            dependency("io.github.oshai:kotlin-logging-jvm:5.1.4")
            dependency("com.mysql:mysql-connector-j:9.0.0")

            // test
            dependency("io.mockk:mockk:1.13.12")

            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

            dependencies {
                // import bom
                dependency("org.testcontainers:testcontainers-bom:1.20.1")
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    noArg {
        annotation("com.shotaste.example.common.framework.annotation.NoArg")
    }

    ktlint {
        outputToConsole.set(true)
        ignoreFailures.set(true) // 違反時途中で止めないようにする
    }
}
