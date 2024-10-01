package com.shotaste.example.common.util.annotation

import com.shotaste.example.common.framework.mybatis.configuration.MyBatisConfiguration
import org.junit.jupiter.api.TestInstance
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MyBatisConfiguration::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
annotation class MapperTest
