package com.shotaste.example.common.domain.service.todo

import com.shotaste.example.common.domain.repository.todo.TodoCategory
import com.shotaste.example.common.domain.repository.todo.TodoInsertDto
import com.shotaste.example.common.domain.repository.todo.TodoRecord
import com.shotaste.example.common.domain.repository.todo.TodoRepository
import com.shotaste.example.common.domain.repository.todo.TodoStatus
import com.shotaste.example.common.domain.repository.todo.fixture
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TodoServiceTest {
    private val todoRepository = mockk<TodoRepository>()
    private val todoService = TodoService(todoRepository)

    @DisplayName("getTodoList: 正しくリストを取得できること")
    @Test
    fun getTodoListTest() {
        val todoList =
            listOf(
                TodoRecord::class.fixture(id = 1),
                TodoRecord::class.fixture(id = 2),
            )
        every { todoRepository.findAll() } returns todoList

        val result = todoService.getTodoList()

        assertThat(result).isEqualTo(todoList)
        verify(exactly = 1) { todoRepository.findAll() }
    }

    @DisplayName("getTodoDetail: 正しく詳細を取得できること")
    @Test
    fun getTodoDetailTest() {
        val todoRecord = TodoRecord::class.fixture()
        every { todoRepository.findById(todoRecord.id) } returns todoRecord

        val result = todoService.getTodoDetail(todoRecord.id)

        assertThat(result).isEqualTo(todoRecord)
        verify(exactly = 1) { todoRepository.findById(todoRecord.id) }
    }

    @DisplayName("createTodo: 正しく作成できること")
    @Test
    fun createTodoTest() {
        val todoInsertDto =
            TodoInsertDto::class.fixture(
                title = "Test Title",
                description = "Test Description",
                status = TodoStatus.TODO,
                category = TodoCategory.WORK,
            )
        every { todoRepository.insert(todoInsertDto) } returns 1

        val result =
            todoService.createTodo(
                title = todoInsertDto.title,
                description = todoInsertDto.description,
                status = todoInsertDto.status.value,
                category = todoInsertDto.category.value,
            )

        assertThat(result).isTrue
        verify(exactly = 1) { todoRepository.insert(todoInsertDto) }
    }

    @DisplayName("updateStatus: 正しくステータスを更新できること")
    @Test
    fun updateStatusTest() {
        every { todoRepository.updateStatusById(1, TodoStatus.DONE) } returns 1

        val result = todoService.updateStatus(1, TodoStatus.DONE.value)

        assertThat(result).isTrue
        verify(exactly = 1) { todoRepository.updateStatusById(1, TodoStatus.DONE) }
    }

    @DisplayName("deleteTodo: 正しく削除できること")
    @Test
    fun deleteTodoTest() {
        every { todoRepository.deleteById(1) } returns 1

        val result = todoService.deleteTodo(1)

        assertThat(result).isTrue
        verify(exactly = 1) { todoRepository.deleteById(1) }
    }
}
