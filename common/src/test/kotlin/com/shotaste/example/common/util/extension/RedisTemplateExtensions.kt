package com.shotaste.example.common.util.extension

import org.springframework.data.redis.core.StringRedisTemplate

fun StringRedisTemplate.flushAll() = keys("*").takeIf { it.isNotEmpty() }?.let { delete(it) }
