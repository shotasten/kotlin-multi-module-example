package com.shotaste.example.common.domain.repository.todo

import com.shotaste.example.common.domain.framework.mybatis.StringEnumBase

enum class TodoCategory(override val value: String) : StringEnumBase<TodoCategory> {
    WORK("work"),
    PRIVATE("private"),
    HOBBY("hobby"),
    OTHER("other"),
    ;

    companion object {
        private val entityMap = entries.associateBy(TodoCategory::value)
        fun from(value: String): TodoCategory {
            return entityMap[value] ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}