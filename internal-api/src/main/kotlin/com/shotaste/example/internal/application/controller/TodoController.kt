package com.shotaste.example.internal.application.controller

import com.shotaste.example.common.domain.repository.todo.TodoCategory
import com.shotaste.example.common.domain.repository.todo.TodoStatus
import com.shotaste.example.common.domain.service.todo.TodoService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class TodoController(
    private val todoService: TodoService,
) {
    @GetMapping("/todo/list")
    fun getTodoList(): ResponseEntity<List<TodoResponse>> {
        val todoList = todoService.getTodoList()
        val response =
            todoList.map {
                TodoResponse(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    status = it.status,
                    category = it.category,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                )
            }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/todo/detail/{id}")
    fun getTodoDetail(
        @PathVariable @Min(1, message = "ID must be a positive integer") id: Int,
    ): ResponseEntity<TodoResponse> {
        val todo = todoService.getTodoDetail(id) ?: return ResponseEntity.notFound().build()
        val response =
            TodoResponse(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                status = todo.status,
                category = todo.category,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt,
            )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/todo/create")
    fun createTodo(
        @Valid @RequestBody request: TodoCreateRequest,
    ): ResponseEntity<Unit> {
        val isSuccess =
            todoService.createTodo(
                title = request.title!!,
                description = request.description,
                status = request.status!!,
                category = request.category!!,
            )

        return if (isSuccess) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/todo/update/status")
    fun updateTodoStatus(
        @Valid @RequestBody request: TodoUpdateStatusRequest,
    ): ResponseEntity<Unit> {
        val isSuccess =
            todoService.updateStatus(
                id = request.id!!,
                status = request.status!!,
            )

        return if (isSuccess) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/todo/delete/{id}")
    fun deleteTodo(
        @PathVariable @Min(1, message = "ID must be a positive integer") id: Int,
    ): ResponseEntity<Unit> {
        val isSuccess = todoService.deleteTodo(id)

        return if (isSuccess) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    data class TodoCreateRequest(
        @field:NotBlank(message = "Title is mandatory")
        @field:Size(max = 30, message = "Title must be less than 30 characters")
        val title: String?,
        @field:Size(max = 100, message = "Description must be less than 100 characters")
        val description: String?,
        @field:NotNull(message = "Status is mandatory")
        @field:Min(value = 1, message = "Status must be at least 1")
        @field:Max(value = 3, message = "Status must be at most 3")
        val status: Int?,
        @field:NotBlank(message = "Category is mandatory")
        val category: String?,
    )

    data class TodoUpdateStatusRequest(
        @field:NotNull(message = "ID is mandatory")
        @field:Min(value = 1, message = "ID must be a positive integer")
        val id: Int?,
        @field:NotNull(message = "Status is mandatory")
        @field:Min(value = 1, message = "Status must be at least 1")
        @field:Max(value = 3, message = "Status must be at most 3")
        val status: Int?,
    )

    data class TodoResponse(
        val id: Int,
        val title: String,
        val description: String?,
        val status: TodoStatus,
        val category: TodoCategory,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}
