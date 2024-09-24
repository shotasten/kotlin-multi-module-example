package com.shotaste.example.common.domain.repository.todo

import com.shotaste.example.common.domain.framework.mybatis.IntEnumBase

enum class TodoStatus(override val value: Int) : IntEnumBase<TodoStatus> {
    TODO(1),
    DOING(2),
    DONE(3),
    ;

    companion object {
        private val entityMap = entries.associateBy(TodoStatus::value)
        fun from(value: Int): TodoStatus {
            return entityMap[value] ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}