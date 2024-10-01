// package com.shotaste.example.common.util.testcontainers
//
// import org.springframework.boot.test.context.TestConfiguration
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection
// import org.springframework.context.annotation.Bean
// import org.testcontainers.containers.GenericContainer
// import org.testcontainers.utility.DockerImageName
//
// @TestConfiguration
// class TestContainerRedisConfig {
//    @Bean
//    @ServiceConnection(name = "redis")
//    fun redisContainer(): GenericContainer<*> {
//        return GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
//            .withExposedPorts(6379)
//    }
//
// }
