package com.shotaste.example.common.domain.repository.todo

import com.shotaste.example.common.framework.mybatis.IntEnumBase

enum class TodoStatus(override val value: Int, val label: String) : IntEnumBase<TodoStatus> {
    TODO(1, "todo"),
    DOING(2, "doing"),
    DONE(3, "done"),
    ;

    companion object {
        private val entityMap = entries.associateBy(TodoStatus::value)

        fun from(value: Int): TodoStatus {
            return entityMap[value] ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}
