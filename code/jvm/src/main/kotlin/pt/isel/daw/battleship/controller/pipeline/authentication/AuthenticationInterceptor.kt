package pt.isel.daw.battleship.controller.pipeline.authentication

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Intercepts the request and checks if the handler requires authentication.
 *
 * If it does then it will check if the request has a valid Authorization Cookie,
 * parse it and inject the user id into the handler arguments.
 */
@Component
class AuthenticationInterceptor(
    private val cookieAuthorizationProcessor: CookieAuthorizationProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.hasMethodAnnotation(Authentication::class.java)) {
            val cookies = request.cookies ?: null
            val userID = cookieAuthorizationProcessor.process(cookies)

            logger.info("${request.method} on ${request.contextPath} authorized by user $userID")

            UserIDArgumentResolver.addUserIDTo(userID, request)
            return true
        }
        return true
    }


    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
    }
}