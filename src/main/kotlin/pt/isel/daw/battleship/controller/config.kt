package pt.isel.daw.battleship.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.daw.battleship.controller.interceptors.authentication.AuthenticationInterceptor


@Component
class AppConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var authenticationInterceptor: AuthenticationInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/api/**")
    }
}