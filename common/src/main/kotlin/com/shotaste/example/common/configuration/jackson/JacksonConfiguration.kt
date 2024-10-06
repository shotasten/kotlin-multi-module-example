package com.shotaste.example.common.configuration.jackson

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            // シリアライズ時にタイムスタンプ形式で出力しない設定
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

            // デフォルトで型情報を含める設定
            activateDefaultTyping(
                // 型情報を含める際のバリデーション設定
                polymorphicTypeValidator,
                // non-finalなクラスに対して型情報を含める設定
                ObjectMapper.DefaultTyping.NON_FINAL,
                // 型情報をJSONプロパティとして含める設定
                JsonTypeInfo.As.PROPERTY,
            )
        }
    }
}
