package com.shotaste.example.internal.application.exception

data class ErrorResponse(
    val code: Int,
    val message: String,
    val path: String,
    val details: List<ErrorDetail>,
) {
    data class ErrorDetail(
        val code: String,
        val message: String,
        val objectName: String,
        val fieldName: String,
    )
}
