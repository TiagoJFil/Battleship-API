package pt.isel.daw.battleship.controller.interceptors

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.daw.battleship.controller.hypermedia.Problem
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

@Component
class InfoInterceptor: HandlerInterceptor {

    private var startTime: Long = 0

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        val paramsString = request.parameterMap.map { "${it.key}=${it.value[0]}" }.joinToString("&")
        val bodyString = request.reader.readText()

        startTime = System.currentTimeMillis()
        logger.info("Request received: ${request.method} ${request.requestURI} with parameters [$paramsString] and body [$bodyString]")
        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {

        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime
        logger.info("Request from ${request.requestURI} took $elapsedTime ms")
        super.postHandle(request, response, handler, modelAndView)
    }

    companion object{
        private val logger = LoggerFactory.getLogger(ResponseEntityExceptionHandler::class.java)
    }

}