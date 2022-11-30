package pt.isel.daw.battleship.controller.pipeline

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenContentType
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity


@ControllerAdvice
class SirenContentTypeResponseAdvice : ResponseBodyAdvice<Any> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        val handlerReturnType = returnType.parameterType
        return SirenEntity::class.java == handlerReturnType
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        response.headers.set("Content-Type", SirenContentType)
        return body
    }

}
