package com.shotaste.example.common.framework.mybatis

interface StringEnumBase<T : Enum<T>> {
    val value: String
}
