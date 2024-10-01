package com.shotaste.example.common.framework.mybatis.configuration

import com.shotaste.example.common.domain.repository.todo.TodoCategory
import com.shotaste.example.common.domain.repository.todo.TodoStatus
import com.shotaste.example.common.framework.mybatis.handler.IntEnumTypeHandler
import com.shotaste.example.common.framework.mybatis.handler.StringEnumTypeHandler
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@MapperScan("com.shotaste.example.common.domain.repository")
class MyBatisConfiguration {
    @Bean
    fun configurationCustomizer(): ConfigurationCustomizer {
        return ConfigurationCustomizer { configuration ->
            // IntベースのEnumとそのハンドラーのマッピング
            val intEnumMappings =
                listOf(
                    TodoStatus::class.java,
                )

            // StringベースのEnumとそのハンドラーのマッピング
            val stringEnumMappings =
                listOf(
                    TodoCategory::class.java,
                )

            // IntベースのEnumTypeHandlerを登録
            intEnumMappings.forEach { enumClass ->
                configuration.typeHandlerRegistry.register(enumClass, IntEnumTypeHandler(enumClass))
            }

            // StringベースのEnumTypeHandlerを登録
            stringEnumMappings.forEach { enumClass ->
                configuration.typeHandlerRegistry.register(enumClass, StringEnumTypeHandler(enumClass))
            }

            // アンスコの含むカラム名をキャメルケースに変換してマッピングする
            configuration.isMapUnderscoreToCamelCase = true
        }
    }
}
