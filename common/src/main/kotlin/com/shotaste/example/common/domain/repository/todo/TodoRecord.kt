package com.shotaste.example.common.domain.repository.todo

import com.shotaste.example.common.domain.framework.annotation.NoArg
import java.time.LocalDateTime

@NoArg
data class TodoRecord(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TodoStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)