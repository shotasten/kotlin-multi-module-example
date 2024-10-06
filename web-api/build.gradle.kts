plugins {
    id("org.springdoc.openapi-gradle-plugin")
}

dependencies {
    // project
    implementation(project(":common"))

    // kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 3rd party
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter")
    implementation("com.mysql:mysql-connector-j")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    // test
    testImplementation(testFixtures(project(":common")))
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

openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs.yaml")
    outputDir.set(file("$rootDir/openapi"))
    outputFileName.set("web-api.yaml")
}
