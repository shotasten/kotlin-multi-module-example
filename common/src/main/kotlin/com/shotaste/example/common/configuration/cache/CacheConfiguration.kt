package com.shotaste.example.common.configuration.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration(
    private val cacheProperties: CacheProperties,
) {
    @Primary
    @Bean
    fun cacheManager(
        redisConnectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): CacheManager {
        // Jacksonのシリアライザを使用
        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)
        val cacheConfig =
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheProperties.ttlSecond)) // キャッシュのTTL設定
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(serializer),
                )
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfig)
            .build()
    }
}
