package com.shotaste.example.common.domain.repository.todo

import com.shotaste.example.common.util.testcontainers.TestContainerRedisInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = [TestContainerRedisInitializer::class])
class TodoRepositoryTest {
    @Autowired
    private lateinit var sut: TodoRepository

    @Test
    fun testFindAll() {
    }
}
