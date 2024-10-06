package com.shotaste.example.internal.controller.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.shotaste.example.common.domain.repository.todo.TodoRecord
import com.shotaste.example.common.domain.repository.todo.fixture
import com.shotaste.example.common.domain.service.todo.TodoService
import com.shotaste.example.internal.application.controller.TodoController
import com.shotaste.example.internal.application.exception.InternalApiCommonExceptionHandler
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@WebMvcTest(TodoController::class)
class TodoControllerTest {
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var todoController: TodoController

    @MockkBean
    private lateinit var todoService: TodoService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val todoRecord = TodoRecord::class.fixture()

    @BeforeEach
    fun beforeEach() {
        mockMvc =
            MockMvcBuilders.standaloneSetup(todoController)
                .setControllerAdvice(InternalApiCommonExceptionHandler()).build()
    }

    @DisplayName("getTodoList: 正しくリストを取得できること")
    @Test
    fun getTodoList() {
        val todoList = listOf(todoRecord)
        every { todoService.getTodoList() } returns todoList

        mockMvc.perform(
            get("/todo/list"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(todoRecord.id))
            .andExpect(jsonPath("$[0].title").value(todoRecord.title))
            .andExpect(jsonPath("$[0].description").value(todoRecord.description))
            .andExpect(jsonPath("$[0].status").value(todoRecord.status.toString()))
            .andExpect(jsonPath("$[0].category").value(todoRecord.category.toString()))

        verify(exactly = 1) { todoService.getTodoList() }
    }

    @DisplayName("getTodoDetail: 正しく詳細を取得できること")
    @Test
    fun getTodoDetail() {
        every { todoService.getTodoDetail(1) } returns todoRecord

        mockMvc.perform(get("/todo/detail/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(todoRecord.id))
            .andExpect(jsonPath("$.title").value(todoRecord.title))
            .andExpect(jsonPath("$.description").value(todoRecord.description))
            .andExpect(jsonPath("$.status").value(todoRecord.status.toString()))
            .andExpect(jsonPath("$.category").value(todoRecord.category.toString()))

        verify(exactly = 1) { todoService.getTodoDetail(1) }
    }

    @DisplayName("createTodo: 正しく作成できること")
    @Test
    fun createTodo() {
        val request =
            TodoController.TodoCreateRequest(
                title = "Test Title",
                description = "Test Description",
                status = 1,
                category = "WORK",
            )
        every {
            todoService.createTodo(
                title = request.title!!,
                description = request.description,
                status = request.status!!,
                category = request.category!!,
            )
        } returns true

        mockMvc.perform(
            post("/todo/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)

        verify(exactly = 1) {
            todoService.createTodo(
                title = request.title!!,
                description = request.description,
                status = request.status!!,
                category = request.category!!,
            )
        }
    }

    @DisplayName("updateTodoStatus: 正しくステータスを更新できること")
    @Test
    fun updateTodoStatus() {
        val request =
            TodoController.TodoUpdateStatusRequest(
                id = 1,
                status = 2,
            )
        every {
            todoService.updateStatus(
                id = request.id!!,
                status = request.status!!,
            )
        } returns true

        mockMvc.perform(
            post("/todo/update/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)

        verify(exactly = 1) {
            todoService.updateStatus(
                id = request.id!!,
                status = request.status!!,
            )
        }
    }

    @DisplayName("deleteTodo: 正しく削除できること")
    @Test
    fun deleteTodo() {
        val id = 1
        every { todoService.deleteTodo(id) } returns true

        mockMvc.perform(
            post("/todo/delete/$id"),
        )
            .andExpect(status().isOk)

        verify(exactly = 1) { todoService.deleteTodo(id) }
    }
}
