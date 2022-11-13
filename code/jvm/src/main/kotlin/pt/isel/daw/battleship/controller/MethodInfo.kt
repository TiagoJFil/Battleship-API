package pt.isel.daw.battleship.controller

data class MethodInfo(private val receivedUri: String, val method: Method) {
    private val port: Int
    private val host: String

    init{
        with(Properties()){
            this@MethodInfo.port = port
            this@MethodInfo.host = host
        }
    }

    val uri = "/api$receivedUri"

    override fun toString(): String {
        return "$method : $uri"
    }

}

enum class Method {
    GET,
    POST,
    PUT,
    DELETE
}