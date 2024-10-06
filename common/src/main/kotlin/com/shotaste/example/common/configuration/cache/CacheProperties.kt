package com.shotaste.example.common.configuration.cache

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.cache")
class CacheProperties(
    val ttlSecond: Long,
)
