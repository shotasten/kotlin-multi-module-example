package com.shotaste.example.common.framework.mybatis.handler

interface StringEnumBase<T : Enum<T>> {
    val value: String
}
