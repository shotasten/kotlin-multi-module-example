package com.shotaste.example.common.framework.mybatis.handler

interface IntEnumBase<T : Enum<T>> {
    val value: Int
}
