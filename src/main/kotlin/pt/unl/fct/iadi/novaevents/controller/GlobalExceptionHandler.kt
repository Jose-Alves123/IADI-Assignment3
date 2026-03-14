package pt.unl.fct.iadi.novaevents.controller

import jakarta.servlet.http.HttpServletResponse
import java.util.NoSuchElementException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice(annotations = [Controller::class])
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(
            exception: NoSuchElementException,
            model: Model,
            response: HttpServletResponse
    ): String {
        response.status = HttpServletResponse.SC_NOT_FOUND
        model.addAttribute("errorMessage", exception.message ?: "Resource not found")
        return "error/404"
    }
}
