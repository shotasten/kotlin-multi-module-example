package com.shotaste.example.common.framework.mybatis

interface IntEnumBase<T : Enum<T>> {
    val value: Int
}
