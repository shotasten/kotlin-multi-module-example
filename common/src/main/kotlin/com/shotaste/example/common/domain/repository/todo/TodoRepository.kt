package com.shotaste.example.common.domain.repository.todo

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class TodoRepository(
    private val todoMapper: TodoMapper,
) {
    @Cacheable("TodoRepository.findAll")
    fun findAll(): List<TodoRecord> {
        return todoMapper.findAll()
    }

    @Cacheable("TodoRepository.findById")
    fun findById(id: Int): TodoRecord? {
        return todoMapper.findById(id)
    }

    fun insert(todoInsertDto: TodoInsertDto): Int {
        return todoMapper.insert(todoInsertDto)
    }

    fun updateStatusById(
        id: Int,
        status: TodoStatus,
    ): Int {
        return todoMapper.updateStatusById(id, status)
    }

    fun deleteById(id: Int): Int {
        return todoMapper.deleteById(id)
    }
}
