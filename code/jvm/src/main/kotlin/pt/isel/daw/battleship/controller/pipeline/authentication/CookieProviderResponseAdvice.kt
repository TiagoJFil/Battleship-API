package pt.isel.daw.battleship.controller.pipeline.authentication

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import pt.isel.daw.battleship.controller.addCookie
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.maxAge
import pt.isel.daw.battleship.controller.path
import pt.isel.daw.battleship.services.entities.AuthInformation
import javax.servlet.http.Cookie

@ControllerAdvice
class CookieProviderResponseAdvice  : ResponseBodyAdvice<Any> {
    companion object {
        const val COOKIE_LIFETIME = 60 * 60 * 24 * 7
        const val COOKIE_USER_ID_NAME = "UID"
    }

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
     val genericTypeName = returnType.genericParameterType.typeName.split("<")[1].split(">")[0]
        val authInfoTypeName = AuthInformation::class.java.typeName
        return genericTypeName == authInfoTypeName
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val authInfo = (body as SirenEntity<AuthInformation>).properties
        require(authInfo != null) { "AuthInformation must not be null" }

        val authCookie = Cookie(CookieAuthorizationProcessor.COOKIE_AUTHORIZATION_NAME, authInfo.token)
            .path("/")
            .maxAge(COOKIE_LIFETIME)
        val userIDCookie = Cookie(COOKIE_USER_ID_NAME, authInfo.uid.toString())
            .path("/")
            .maxAge(COOKIE_LIFETIME)

        response.addCookie(authCookie)
        response.addCookie(userIDCookie)
        return body
    }

}