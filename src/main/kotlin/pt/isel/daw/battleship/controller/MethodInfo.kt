package pt.isel.daw.battleship.controller

data class MethodInfo(private val receivedUri: String, val method: Method) {
    private val port = Properties().port
    private val host = Properties().host

    val uri = "http://${host}:${port}/api$receivedUri"

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