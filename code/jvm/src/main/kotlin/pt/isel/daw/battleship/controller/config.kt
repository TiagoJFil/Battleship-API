package pt.isel.daw.battleship.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.daw.battleship.controller.interceptors.authentication.AuthenticationInterceptor
import pt.isel.daw.battleship.controller.interceptors.authentication.UserIDArgumentResolver


@Component
class AppConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var authenticationInterceptor: AuthenticationInterceptor


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserIDArgumentResolver())
        return super.addArgumentResolvers(resolvers)
    }
}
