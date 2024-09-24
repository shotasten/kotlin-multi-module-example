dependencies {
    // project
    implementation(project(":common"))

    // kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))

    // spring
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // 3rd party
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}