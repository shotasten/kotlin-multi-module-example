package com.shotaste.example.common.util.testcontainers

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.util.ResourceUtils
import java.sql.Connection

object TestContainerDataSourceInitializer {
    private val log = KotlinLogging.logger {}

    private const val BASE_PROJECT_NAME = "kotlin-multi-module-example"
    private const val PROJECT_NAME = "common"
    private const val SQL_PATH = "/src/main/resources/db/mysql"

    @Throws(Exception::class)
    @JvmStatic
    fun setup(connection: Connection) {
        try {
            val sqlFilePath = buildSqlFilePath()
            executeSqlScripts(connection, sqlFilePath)
        } catch (e: Exception) {
            log.error(e) { "Failed to initialize the database" }
            throw e
        }
    }

    private fun buildSqlFilePath(): String {
        val classPathRootPath = ClassPathResource("").file.absolutePath
        return (
            classPathRootPath.substring(0, classPathRootPath.indexOf(BASE_PROJECT_NAME)) +
                BASE_PROJECT_NAME +
                "/" +
                PROJECT_NAME +
                SQL_PATH
        )
    }

    private fun executeSqlScripts(
        connection: Connection,
        sqlFilePath: String,
    ) {
        val sqlDirectory = ResourceUtils.getFile(sqlFilePath)
        sqlDirectory.list()!!
            .sorted()
            .forEach {
                val scriptPath = "$sqlFilePath/$it"
                log.info { "Executing SQL script: $scriptPath" }
                ScriptUtils.executeSqlScript(connection, FileSystemResource(scriptPath))
            }
    }
}
