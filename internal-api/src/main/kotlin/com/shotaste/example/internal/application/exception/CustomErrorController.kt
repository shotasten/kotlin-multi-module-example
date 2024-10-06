package com.shotaste.example.internal.application.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.ServletWebRequest

@Controller
@RequestMapping("\${server.error.path:\${error.path:/error}}")
class CustomErrorController(
    private val errorAttributes: ErrorAttributes,
) : ErrorController {
    @RequestMapping
    fun error(request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorMap = errorAttributes.getErrorAttributes(ServletWebRequest(request), ErrorAttributeOptions.defaults())
        val status = HttpStatus.valueOf(errorMap["status"] as Int)
        val errorResponse =
            ErrorResponse(
                code = status.value(),
                message = errorMap["error"].toString(),
                path = errorMap["path"].toString(),
                details = emptyList(),
            )
        return ResponseEntity(errorResponse, status)
    }
}
