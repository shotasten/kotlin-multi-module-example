package com.shotaste.example.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.shotaste.example"])
class BatchApplication

fun main(args: Array<String>) {
	runApplication<BatchApplication>(*args)
}
