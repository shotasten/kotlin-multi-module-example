plugins {
	kotlin("jvm") version "2.0.20"
	kotlin("plugin.spring") version "2.0.20"
	kotlin("plugin.noarg") version "2.0.20"
	id("org.springframework.boot") version "3.3.4" apply false
	id("io.spring.dependency-management") version "1.1.6"
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

			// test
			dependency("io.mockk:mockk:1.13.12")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	noArg {
		annotation("com.shotaste.example.common.domain.framework.annotation.NoArg")
	}
}
