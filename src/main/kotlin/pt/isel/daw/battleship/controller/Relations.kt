package pt.isel.daw.battleship.controller

import org.springframework.web.util.UriTemplate
import pt.isel.daw.battleship.controller.hypermedia.SirenAction
import pt.isel.daw.battleship.controller.hypermedia.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.selfLink


private data class SirenInfo(val title: String, val action :SirenAction)

private val UriRelationsMap = mutableMapOf<String, List<String>>(
    Uris.Lobby.QUEUE to listOf(
        Uris.HOME,Uris.Lobby.CANCEL_QUEUE, Uris.Game.LAYOUT_DEFINITION
    ),
    Uris.Lobby.CANCEL_QUEUE to listOf(
        Uris.HOME, Uris.Lobby.QUEUE
    ),
    Uris.SYSTEM_INFO to listOf(
        Uris.HOME
    ),
    Uris.STATISTICS to listOf(
        Uris.HOME
    ),
    Uris.User.LOGIN to listOf(
        Uris.HOME, Uris.User.REGISTER
    ),
    Uris.User.REGISTER to listOf(
        Uris.HOME, Uris.User.LOGIN
    ),
)

private val ActionRelationsMap = mutableMapOf<String,SirenInfo>(
    Uris.Lobby.QUEUE to SirenInfo(
        "Game",
        SirenAction(
            name = "play-intent",
            href = "http://localhost:8080/api/lobby/",
            method = "POST",
            type = "application/json",
            fields = listOf()
        )
    ),
    Uris.Lobby.CANCEL_QUEUE to SirenInfo(
        "Home",
        SirenAction(
            name = "cancel-queue",
            method = "POST",
            href = "http://localhost:8080/api/cancel",
            type = "application/json"
        )
    ),
    Uris.HOME to SirenInfo(
        "Home",
        SirenAction(
            name = "home",
            href = "http://localhost:8080/api",
            method = "GET",
            fields = listOf()
        )
    ),
    Uris.Game.LAYOUT_DEFINITION to SirenInfo(
        "FleetLayout",
        SirenAction(
            name = "place-ships",
            href = "http://localhost:8080/api/game/{gameId}/place-ships",
            method = "POST",
            type = "application/json",
            fields = listOf()
        )
    ),
    Uris.SYSTEM_INFO to SirenInfo(
        "System Info",
        SirenAction(
            name = "system-info",
            href = "http://localhost:8080/api/sysinfo",
            method = "GET",
            fields = listOf()
        )
    ),
    Uris.STATISTICS to SirenInfo(
        "Statistics",
        SirenAction(
            name = "statistics",
            href = "http://localhost:8080/api/statistics",
            method = "GET",
            fields = listOf()
        )
    ),
    Uris.User.LOGIN to SirenInfo(
        "Login",
        SirenAction(
            name = "login",
            href = "http://localhost:8080/api/login",
            method = "POST",
            type = "application/json",
            fields = listOf(
                SirenAction.Field(name = "username", type = "text"),
                SirenAction.Field(name = "password", type = "password")
            )
        )
    ),
    Uris.User.REGISTER to SirenInfo(
        "Register",
        SirenAction(
            name = "register",
            href = "http://localhost:8080/api/",
            method = "POST",
            type = "application/json",
            fields = listOf(
                SirenAction.Field(name = "username", type = "text"),
                SirenAction.Field(name = "password", type = "password")
            )
        )
    ),
)

private fun updateHref(action: SirenAction, uriVariables: Map<String,String>?): SirenAction {
    if(uriVariables.isNullOrEmpty()) return action
    val template = UriTemplate(action.href)
    val uri = template.expand(uriVariables)
    return action.copy(href = uri.toString())
}


private fun getActions(uri: String, uriVariables: Map<String,String>?) =
    UriRelationsMap[uri]?.map { rel ->
        val action =ActionRelationsMap[rel]?.action ?: throw IllegalArgumentException("No action found for $rel")
        updateHref(action,uriVariables)
    }

private fun getTitle(uri: String) =
    ActionRelationsMap[uri]?.title ?: throw IllegalArgumentException("No action found for $uri")


fun <T> T.toSiren(uri: String, uriVariables: Map<String,String>? = null): SirenEntity<T> {

    return SirenEntity(
        title = getTitle(uri),
        properties = this,
        actions = getActions(uri,uriVariables),
        links = listOf(selfLink(uri)),
        entities = listOf()
        )
}


