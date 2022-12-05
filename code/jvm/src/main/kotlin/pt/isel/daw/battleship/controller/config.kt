package pt.isel.daw.battleship.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.daw.battleship.controller.pipeline.authentication.AuthenticationInterceptor
import pt.isel.daw.battleship.controller.pipeline.authentication.UserIDArgumentResolver


@Component
class AppConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var authenticationInterceptor: AuthenticationInterceptor

    @Autowired
    private lateinit var userIDArgumentResolver: UserIDArgumentResolver


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userIDArgumentResolver)
        return super.addArgumentResolvers(resolvers)
    }

    //temporary needed for development on the frontend
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8080").allowedMethods("*")
    }
}
