package com.shotaste.example.common.domain.framework.mybatis

interface IntEnumBase<T : Enum<T>> {
    val value: Int
}
