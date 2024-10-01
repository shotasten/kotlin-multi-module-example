package com.shotaste.example.common.util.testcontainers

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer

object TestContainerRedisInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val redisContainer: GenericContainer<*> =
        GenericContainer("redis:6-alpine")
            .withExposedPorts(6379)

    init {
        redisContainer.start()
    }

    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.data.redis.host=${redisContainer.host}",
            "spring.data.redis.port=${redisContainer.firstMappedPort}",
        ).applyTo(configurableApplicationContext.environment)
    }
}
