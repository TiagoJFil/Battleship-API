package pt.isel.daw.battleship.controller.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.IOException
import java.io.UnsupportedEncodingException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse



@Component
class LoggingFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val startTime = System.currentTimeMillis()
        filterChain.doFilter(requestWrapper, response)
        val timeTaken = System.currentTimeMillis() - startTime
        val requestBody = getStringValue(
            requestWrapper.contentAsByteArray,
            request.characterEncoding
        )
        val paramsString = request.parameterMap.map { "${it.key}=${it.value[0]}" }.joinToString("&")

        LOGGER.info(
            "Request ${request.method} on ${request.requestURI} took $timeTaken millis with params: |$paramsString| and body: |$requestBody|",
        )
    }

    private fun getStringValue(contentAsByteArray: ByteArray, characterEncoding: String): String {
        try {
            return String(contentAsByteArray, charset(characterEncoding))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return ""
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoggingFilter::class.java)
    }
}