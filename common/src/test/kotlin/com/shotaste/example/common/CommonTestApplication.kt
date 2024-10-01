package com.shotaste.example.common

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CommonTestApplication

fun main(args: Array<String>) {
    runApplication<CommonTestApplication>(*args)
}
