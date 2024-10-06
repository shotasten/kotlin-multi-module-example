package com.shotaste.example.common.domain.repository.todo

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.shotaste.example.common.framework.annotation.NoArg
import java.time.LocalDateTime

// @JsonTypeInfoを使用して、JSONシリアライズ時に型情報を含める
// コレクションを伴わないcacheのときに省略されるので必要
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class",
)
@NoArg
data class TodoRecord(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TodoStatus,
    val category: TodoCategory,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
