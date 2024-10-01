package com.shotaste.example.webapi.application.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.method.ParameterValidationResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class WebApiCommonExceptionHandler {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    @ExceptionHandler(ServletRequestBindingException::class)
    fun handleServletRequestBindingException(e: ServletRequestBindingException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(
            status = HttpStatus.BAD_REQUEST,
            message = e.message,
            bindingResult = null,
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(
            status = HttpStatus.BAD_REQUEST,
            message = e.message,
            bindingResult = null,
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(
            status = HttpStatus.BAD_REQUEST,
            message = e.message,
            bindingResult = null,
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed.",
            bindingResult = e.bindingResult,
        )
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ErrorResponse> {
        return buildResponseEntity(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed.",
            bindingResult = e.bindingResult,
        )
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(e: HandlerMethodValidationException): ResponseEntity<ErrorResponse> {
        return if (e.allValidationResults.isNotEmpty()) {
            buildErrorResponse(
                status = HttpStatus.BAD_REQUEST,
                message = e.reason,
                validationResults = e.allValidationResults,
            )
        } else {
            val bindingResult = (e.cause as? MethodArgumentNotValidException)?.bindingResult
            buildResponseEntity(
                status = HttpStatus.BAD_REQUEST,
                message = "Validation failed.",
                bindingResult = bindingResult,
            )
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val message = "Argument type mismatch: ${e.name} should be of type ${e.requiredType?.simpleName}"
        return buildResponseEntity(
            status = HttpStatus.BAD_REQUEST,
            message = message,
            bindingResult = null,
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e) { "${e.message}" }
        return buildResponseEntity(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = e.message,
            bindingResult = null,
        )
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

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String?,
        validationResults: List<ParameterValidationResult>,
    ): ResponseEntity<ErrorResponse> {
        val errorDetails = mutableListOf<ErrorResponse.ErrorDetail>()

        for (validationResult in validationResults) {
            for (resolvableErrors in validationResult.resolvableErrors) {
                val fieldName =
                    (resolvableErrors.arguments?.firstOrNull { it is DefaultMessageSourceResolvable } as? DefaultMessageSourceResolvable)?.defaultMessage
                val errorDetail =
                    ErrorResponse.ErrorDetail(
                        code = resolvableErrors.codes?.last() ?: "UNKNOWN",
                        message = resolvableErrors.defaultMessage ?: "No message available",
                        objectName = "",
                        fieldName = fieldName ?: "UNKNOWN",
                    )
                errorDetails.add(errorDetail)
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
