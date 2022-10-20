package pt.isel.daw.battleship.controller.interceptors.authentication

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.daw.battleship.utils.UserID
import javax.servlet.http.HttpServletRequest

@Component
class UserIDArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserID::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        return request?.let { getUserIDFrom(it) }
    }

    companion object{
        private const val KEY = "UserIDArgumentResolver"

        fun addUserIDTo(user: UserID, request: HttpServletRequest) {
            return request.setAttribute(KEY, user)
        }

        fun getUserIDFrom(request: HttpServletRequest): UserID? {
            return request.getAttribute(KEY)?.let {
                it as? UserID
            }
        }
    }
}