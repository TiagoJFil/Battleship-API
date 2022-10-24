package pt.isel.daw.battleship.controller.interceptors

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import pt.isel.daw.battleship.controller.hypermedia.SirenContentType
import pt.isel.daw.battleship.controller.hypermedia.SirenEntity


@ControllerAdvice
class ContentTypeResponseAdvice : ResponseBodyAdvice<Any> {
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


//@Component
//class ContentTypeInterceptor : HandlerInterceptor {
//    override fun postHandle(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        handler: Any,
//        modelAndView: ModelAndView?
//    ) {
//        val hand = handler is HandlerMethod
//
//
//        val wrapper = object : HttpServletResponseWrapper(response) {
//            override fun setContentType(type: String?) {
//                super.setContentType(SirenContentType)
//            }
//        }
//        //put the content type in the response header
//        if (handler is HandlerMethod) {
//            val returnType = handler.method.returnType
//            if (returnType == SirenEntity::class.java) {
//                wrapper.contentType = SirenContentType
//            }
//        }
//        val d = wrapper.getHeader("Content-Type")
//        super.postHandle(request, wrapper, handler, modelAndView)
//    }
//}