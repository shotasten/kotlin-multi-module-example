package com.shotaste.example.common.domain.repository.todo

import java.time.LocalDateTime
import kotlin.reflect.KClass

fun KClass<TodoRecord>.fixture(
    id: Int = 0,
    title: String = "default title",
    description: String? = "default description",
    status: TodoStatus = TodoStatus.TODO,
    category: TodoCategory = TodoCategory.WORK,
    createdAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 10, 0),
    updatedAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 10, 0),
): TodoRecord {
    return TodoRecord(
        id = id,
        title = title,
        description = description,
        status = status,
        category = category,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun KClass<TodoInsertDto>.fixture(
    title: String = "default title",
    description: String? = "default description",
    status: TodoStatus = TodoStatus.TODO,
    category: TodoCategory = TodoCategory.WORK,
): TodoInsertDto {
    return TodoInsertDto(
        title = title,
        description = description,
        status = status,
        category = category,
    )
}
