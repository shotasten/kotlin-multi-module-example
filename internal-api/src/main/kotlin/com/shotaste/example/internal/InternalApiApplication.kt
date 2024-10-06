package com.shotaste.example.internal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.shotaste.example"])
@ConfigurationPropertiesScan(basePackages = ["com.shotaste.example"])
class WebApiApplication

fun main(args: Array<String>) {
    runApplication<WebApiApplication>(*args)
}
