package com.shotaste.example.common.domain.repository.todo

import com.ninja_squad.dbsetup_kotlin.dbSetup
import com.ninja_squad.dbsetup_kotlin.mappedValues
import com.shotaste.example.common.util.annotation.MapperTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.lang.Thread.sleep
import java.time.LocalDateTime
import javax.sql.DataSource
import kotlin.test.junit5.JUnit5Asserter.fail

@MapperTest
class TodoMapperTest {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var sut: TodoMapper

    @BeforeAll
    fun beforeAll() {
        // テストデータのセットアップ
        dbSetup(to = dataSource) {
            truncate(TABLE_NAME)

            insertInto(TABLE_NAME) {
                mappedValues(
                    "id" to testData1.id,
                    "title" to testData1.title,
                    "description" to testData1.description,
                    "status" to testData1.status.value,
                    "category" to testData1.category.value,
                    "created_at" to testData1.createdAt,
                    "updated_at" to testData1.updatedAt,
                )
                mappedValues(
                    "id" to testData2.id,
                    "title" to testData2.title,
                    "description" to testData2.description,
                    "status" to testData2.status.value,
                    "category" to testData2.category.value,
                    "created_at" to testData2.createdAt,
                    "updated_at" to testData2.updatedAt,
                )
                mappedValues(
                    "id" to testData3.id,
                    "title" to testData3.title,
                    "description" to testData3.description,
                    "status" to testData3.status.value,
                    "category" to testData3.category.value,
                    "created_at" to testData3.createdAt,
                    "updated_at" to testData3.updatedAt,
                )
            }
        }.launch()
    }

    @Test
    fun testFindAll() {
        // execute
        val actual = sut.findAll()

        // verify
        assertThat(actual).containsExactlyInAnyOrder(testData1, testData2, testData3)
    }

    @Test
    fun testFindById() {
        // prepare
        val testDataId1 = testData1.id

        // execute
        val actual = sut.findById(testDataId1)

        // verify
        assertThat(actual).isEqualTo(testData1)
    }

    @Test
    fun testInsert() {
        // prepare
        val title = "Task insert"
        val description = "Description for Task insert"
        val status = TodoStatus.TODO
        val category = TodoCategory.WORK
        val insertData =
            TodoInsertDto(
                title = title,
                description = description,
                status = status,
                category = category,
            )

        // execute
        val result = sut.insert(insertData)

        val insertedDataId = insertData.id ?: fail("insertedDataId is null")

        // verify
        val insertedData = sut.findById(result) ?: fail("insertedData is null")
        assertThat(result).isEqualTo(1)

        val actual = sut.findById(insertedDataId) ?: fail("actual is null")
        assertThat(actual).isNotNull
        assertThat(actual.title).isEqualTo(title)
        assertThat(actual.description).isEqualTo(description)
        assertThat(actual.status).isEqualTo(status)
        assertThat(actual.category).isEqualTo(category)
    }

    @Test
    fun testUpdateStatusById() {
        // prepare
        val title = "Task update"
        val description = "Description for Task update"
        val status = TodoStatus.DOING
        val category = TodoCategory.HOBBY
        val insertData =
            TodoInsertDto(
                title = title,
                description = description,
                status = status,
                category = category,
            )
        // insert
        sut.insert(insertData)

        val expectedStatus = TodoStatus.DONE
        val insertedDataId = insertData.id ?: fail("insertedDataId is null")

        // created_atとupdated_atが同じになるのを防ぐために1秒待つ
        sleep(1000)

        // execute
        val actual = sut.updateStatusById(insertedDataId, expectedStatus)

        // verify
        val updatedData = sut.findById(insertedDataId) ?: fail("updatedData is null")
        assertThat(actual).isEqualTo(1)
        assertThat(updatedData).isNotNull
        // 更新されていること
        assertThat(updatedData.status).isEqualTo(expectedStatus)
        assertThat(updatedData.updatedAt).isAfter(updatedData.createdAt)
        // 他のカラムは変更されていないこと
        assertThat(updatedData.title).isEqualTo(title)
        assertThat(updatedData.description).isEqualTo(description)
        assertThat(updatedData.category).isEqualTo(category)
    }

    @Test
    fun testDeleteById() {
        // prepare
        val title = "Task delete"
        val description = "Description for Task delete"
        val status = TodoStatus.DOING
        val category = TodoCategory.HOBBY
        val insertData =
            TodoInsertDto(
                title = title,
                description = description,
                status = status,
                category = category,
            )
        // insert
        val insertCount = sut.insert(insertData)

        val insertedDataId = insertData.id ?: fail("insertedDataId is null")

        // execute
        val actual = sut.deleteById(insertedDataId)

        // verify
        assertThat(insertCount).isEqualTo(1)
        val deletedData = sut.findById(insertedDataId)
        assertThat(actual).isEqualTo(1)
        assertThat(deletedData).isNull()
    }

    companion object {
        private const val TABLE_NAME = "todo"
        private val testLocalDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
        private val testData1 =
            TodoRecord(
                id = 1,
                title = "Task 1",
                description = "Description for Task 1",
                status = TodoStatus.TODO,
                category = TodoCategory.WORK,
                createdAt = testLocalDate,
                updatedAt = testLocalDate,
            )
        private val testData2 =
            TodoRecord(
                id = 2,
                title = "Task 2",
                description = "Description for Task 2",
                status = TodoStatus.DOING,
                category = TodoCategory.HOBBY,
                createdAt = testLocalDate,
                updatedAt = testLocalDate,
            )
        private val testData3 =
            TodoRecord(
                id = 3,
                title = "Task 3",
                description = "Description for Task 3",
                status = TodoStatus.DONE,
                category = TodoCategory.OTHER,
                createdAt = testLocalDate,
                updatedAt = testLocalDate,
            )
    }
}
