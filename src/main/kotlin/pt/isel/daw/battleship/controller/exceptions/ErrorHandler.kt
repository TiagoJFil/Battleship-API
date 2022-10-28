package pt.isel.daw.battleship.controller.exceptions

import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.daw.battleship.controller.hypermedia.Problem
import pt.isel.daw.battleship.controller.hypermedia.setProblemHeader
import pt.isel.daw.battleship.services.exception.AppException
import pt.isel.daw.battleship.services.exception.ErrorTypes
import java.net.URI


@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException, servletRequest: ServletWebRequest): ResponseEntity<Problem> {

        val problem = Problem(
            ex.type?.let { URI(it) },
            ex.message,
            instance = servletRequest.request.requestURI.toString()
        )

        logger.error(" $ex : ${problem.title} on ${servletRequest.contextPath}")
        return ResponseEntity.status(errorToStatusMap[ex::class] ?: HttpStatus.INTERNAL_SERVER_ERROR)
            .setProblemHeader()
            .body(problem)
    }

    /**
     * Handle the exception thrown when a handler is not found for a specific request.
     */
    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {

        return ResponseEntity
            .status(404)
            .setProblemHeader()
            .body(Problem(
                URI(ErrorTypes.General.NOT_FOUND),
                ex.message,
                instance = (request as ServletWebRequest).request.requestURI.toString()
            ))
    }

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity
            .status(405)
            .setProblemHeader()
            .body(Problem(
                URI(ErrorTypes.General.METHOD_NOT_ALLOWED),
                ex.message,
                instance = (request as ServletWebRequest).request.requestURI.toString()
            ))
    }



    companion object{
        private val logger =  LoggerFactory.getLogger(ErrorHandler::class.java)
    }

}