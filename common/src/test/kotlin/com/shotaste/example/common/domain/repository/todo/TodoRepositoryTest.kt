package com.shotaste.example.common.domain.repository.todo

import com.ninjasquad.springmockk.MockkBean
import com.shotaste.example.common.util.extension.flushAll
import com.shotaste.example.common.util.testcontainers.TestContainerRedisInitializer
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.ContextConfiguration
import java.lang.Thread.sleep

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = [TestContainerRedisInitializer::class])
class TodoRepositoryTest {
    @Autowired
    private lateinit var sut: TodoRepository

    @MockkBean
    private lateinit var todoMapper: TodoMapper

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var cacheManager: CacheManager

    @BeforeEach
    fun beforeEach() {
        redisTemplate.flushAll()
    }

    @DisplayName("findAll：キャッシュから取得できること")
    @Test
    fun findAllTest() {
        val record1 = TodoRecord::class.fixture(id = 1)
        val record2 = TodoRecord::class.fixture(id = 2)
        val record3 = TodoRecord::class.fixture(id = 3)
        val recordList1st = listOf(record1, record2)
        val recordList2nd = listOf(record1, record2, record3)

        every { todoMapper.findAll() } returns recordList1st andThen recordList2nd

        val actual1st = sut.findAll()
        val actual2nd = sut.findAll()

        // cache ttl 以上待つ
        sleep(1010)

        val actual3rd = sut.findAll()

        assertThat(actual1st).isEqualTo(recordList1st)
        // キャッシュから取得されていること
        assertThat(actual2nd).isEqualTo(recordList1st)
        // キャッシュがクリアされていること
        assertThat(actual3rd).isEqualTo(recordList2nd)
        verify(exactly = 2) { todoMapper.findAll() }
    }

    @DisplayName("findById：キャッシュから取得できること")
    @Test
    fun findByIdTest() {
        val record1 =
            TodoRecord::class.fixture(
                id = 1,
                status = TodoStatus.TODO,
            )
        val record2 =
            TodoRecord::class.fixture(
                id = 1,
                status = TodoStatus.DONE,
            )

        every { todoMapper.findById(1) } returns record1 andThen record2
        every { todoMapper.findById(2) } returns null

        val actual1st = sut.findById(1)
        val actual2nd = sut.findById(1)
        val actualAnother = sut.findById(2)
        redisTemplate.flushAll()
        val actual3rd = sut.findById(1)

        assertThat(actual1st).isEqualTo(record1)
        // キャッシュから取得されていること
        assertThat(actual2nd).isEqualTo(record1)
        // キー違うときキャッシュされていないこと
        assertThat(actualAnother).isNull()
        // キャッシュがクリアされていること
        assertThat(actual3rd).isEqualTo(record2)

        verify(exactly = 2) { todoMapper.findById(1) }
        verify(exactly = 1) { todoMapper.findById(2) }
    }

    @DisplayName("insert: 正しくmapperが呼ばれていること")
    @Test
    fun insertTest() {
        val todoInsertDto = TodoInsertDto::class.fixture()
        every { todoMapper.insert(todoInsertDto) } returns 1

        val result = sut.insert(todoInsertDto)

        assertThat(result).isEqualTo(1)
        verify(exactly = 1) { todoMapper.insert(todoInsertDto) }
    }

    @DisplayName("updateStatusById: 正しくmapperが呼ばれていること")
    @Test
    fun updateStatusByIdTest() {
        val id = 1
        val status = TodoStatus.DONE
        every { todoMapper.updateStatusById(id, status) } returns 1

        val result = sut.updateStatusById(id, status)

        assertThat(result).isEqualTo(1)
        verify(exactly = 1) { todoMapper.updateStatusById(id, status) }
    }

    @DisplayName("deleteById: 正しくmapperが呼ばれていること")
    @Test
    fun deleteByIdTest() {
        val id = 1
        every { todoMapper.deleteById(id) } returns 1

        val result = sut.deleteById(id)

        assertThat(result).isEqualTo(1)
        verify(exactly = 1) { todoMapper.deleteById(id) }
    }
}
