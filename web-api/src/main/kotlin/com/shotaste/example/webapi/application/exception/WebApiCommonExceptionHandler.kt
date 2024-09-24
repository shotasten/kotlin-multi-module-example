package com.shotaste.example.webapi.application.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class WebApiCommonExceptionHandler {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    @ExceptionHandler(ServletRequestBindingException::class)
    fun handleServletRequestBindingException(e: ServletRequestBindingException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, e.message, null)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, e.message, null)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, e.message, null)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed.", e.bindingResult)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed.", e.bindingResult)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e) { "${e.message}" }
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.message, null)
    }

    private fun buildResponseEntity(
        status: HttpStatus,
        message: String?,
        bindingResult: BindingResult?,
    ): ResponseEntity<ErrorResponse> {
        val errorDetails = mutableListOf<ErrorResponse.ErrorDetail>()

        if (bindingResult != null) {
            for (objectError in bindingResult.allErrors) {
                if (objectError is FieldError) {
                    val errorDetail =
                        ErrorResponse.ErrorDetail(
                            code = objectError.code ?: "UNKNOWN",
                            message = objectError.defaultMessage ?: "No message available",
                            objectName = objectError.objectName,
                            fieldName = objectError.field,
                        )
                    errorDetails.add(errorDetail)
                }
            }
        }

        val errorResponse =
            ErrorResponse(
                code = status.value(),
                message = message ?: "No message available",
                details = errorDetails,
            )

        return ResponseEntity(errorResponse, status)
    }
}
