package com.shotaste.example.common.domain.service.todo

import com.shotaste.example.common.domain.repository.todo.TodoCategory
import com.shotaste.example.common.domain.repository.todo.TodoInsertDto
import com.shotaste.example.common.domain.repository.todo.TodoMapper
import com.shotaste.example.common.domain.repository.todo.TodoRecord
import com.shotaste.example.common.domain.repository.todo.TodoStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class TodoService(
    private val todoMapper: TodoMapper,
) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    @Transactional(readOnly = true)
    fun getTodoList(): List<TodoRecord> {
        val list = todoMapper.findAll()
        log.info { "Todo list: $list" }
        return list
    }

    @Transactional(readOnly = true)
    fun getTodoDetail(id: Int) = todoMapper.findById(id)

    fun createTodo(
        title: String,
        description: String?,
        status: Int,
        category: String,
    ): Boolean {
        val todoStatus = TodoStatus.from(status)
        val todoCategory = TodoCategory.from(category)
        val dto =
            TodoInsertDto(
                title = title,
                description = description,
                status = todoStatus,
                category = todoCategory,
            )

        val isSuccess = todoMapper.insert(dto) > 0

        if (isSuccess) {
            log.info { "Todo created #${dto.id}: $dto" }
        } else {
            log.error { "Failed to create todo: $dto" }
        }

        return isSuccess
    }

    fun updateStatus(
        id: Int,
        status: Int,
    ): Boolean {
        val isSuccess = todoMapper.updateStatusById(id = id, status = TodoStatus.from(status)) > 0

        if (isSuccess) {
            log.info { "Todo status updated: id=$id, status=$status" }
        } else {
            log.error { "Failed to update todo status: id=$id, status=$status" }
        }

        return isSuccess
    }

    fun deleteTodo(id: Int): Boolean {
        val isSuccess = todoMapper.deleteById(id) > 0

        if (isSuccess) {
            log.info { "Todo deleted: id=$id" }
        } else {
            log.error { "Failed to delete todo: id=$id" }
        }

        return isSuccess
    }
}
