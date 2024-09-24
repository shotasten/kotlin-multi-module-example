package com.shotaste.example.common.domain.repository.todo

data class TodoInsertDto(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val status: TodoStatus,
    val category: TodoCategory,
)
