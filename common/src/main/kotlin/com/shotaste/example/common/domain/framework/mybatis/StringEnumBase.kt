package com.shotaste.example.common.domain.framework.mybatis

interface StringEnumBase<T : Enum<T>> {
    val value: String
}