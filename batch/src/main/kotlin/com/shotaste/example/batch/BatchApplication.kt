package com.shotaste.example.batch

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.shotaste.example"])
@MapperScan("com.shotaste.example.common.domain.repository")
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}
