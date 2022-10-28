package pt.isel.daw.battleship.controller

import org.springframework.web.util.UriTemplate
import pt.isel.daw.battleship.controller.hypermedia.SirenAction
import pt.isel.daw.battleship.controller.hypermedia.SirenAction.*
import pt.isel.daw.battleship.controller.hypermedia.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.SirenLink
import pt.isel.daw.battleship.controller.hypermedia.selfLink


data class SirenInfo(
    val name: String,
    val href: String,
    val method: String? = null,
    val type: String? = null,
    val fields: List<Field>? = null,
    val rel: List<String> = emptyList(),
    val title: String
) {
    fun toLink() = SirenLink(
        rel = rel,
        href = href,
        type = type,
        title = title
    )

    fun toAction() = SirenAction(
        name = name,
        href = href,
        method = method,
        type = type,
        fields = fields
    )

}

private const val mediaType = "application/json"

private val UriActionsRelationsMap = mutableMapOf<String, List<String>>(
    Uris.HOME to listOf(
        Uris.User.LOGIN, Uris.User.REGISTER
    ),
    Uris.Lobby.QUEUE to listOf(
        Uris.Lobby.CANCEL_QUEUE, Uris.Game.LAYOUT_DEFINITION
    ),
    Uris.Lobby.CANCEL_QUEUE to listOf(
        Uris.Lobby.QUEUE
    ),
    Uris.User.LOGIN to listOf(
        Uris.User.REGISTER
    ),
    Uris.User.REGISTER to listOf(
        Uris.User.LOGIN
    ),
    Uris.Game.LAYOUT_DEFINITION to listOf(
        Uris.Game.SHOTS_DEFINITION
    ),
    Uris.Game.OPPONENT_FLEET to listOf(
        Uris.Game.SHOTS_DEFINITION
    ),
)

private val UriLinksRelationsMap = mutableMapOf<String, List<String>>(
    Uris.HOME to listOf(
        Uris.SYSTEM_INFO, Uris.STATISTICS
    ),
    Uris.Lobby.QUEUE to listOf(
        Uris.HOME
    ),
    Uris.Lobby.CANCEL_QUEUE to listOf(
        Uris.HOME
    ),
    Uris.User.LOGIN to listOf(
        Uris.HOME
    ),
    Uris.User.REGISTER to listOf(
        Uris.HOME
    ),
    Uris.Game.LAYOUT_DEFINITION to listOf(
        Uris.HOME, Uris.Game.GAME_STATE, Uris.Game.MY_FLEET, Uris.Game.OPPONENT_FLEET
    ),
    Uris.Game.OPPONENT_FLEET to listOf(
        Uris.HOME, Uris.Game.GAME_STATE, Uris.Game.MY_FLEET
    ),
    Uris.Game.MY_FLEET to listOf(
        Uris.HOME, Uris.Game.GAME_STATE, Uris.Game.OPPONENT_FLEET
    ),
    Uris.Game.SHOTS_DEFINITION to listOf(
        Uris.HOME, Uris.Game.GAME_STATE, Uris.Game.OPPONENT_FLEET
    )
)

private val SirenInfoMap = mutableMapOf<String, SirenInfo>(
    Uris.HOME to SirenInfo(
        name = "home",
        href = "http://localhost:8080/api/",
        method = "GET",
        type = mediaType,
        fields = listOf(),
        rel = listOf("home"),
        title = "Home"
    ),
    Uris.Lobby.QUEUE to SirenInfo(
            name = "play-intent",
            href = "http://localhost:8080/api/lobby/",
            method = "POST",
            type = mediaType,
            fields = listOf(),
            rel = listOf("play-intent"),
            title = "Play Intent"
    ),
    Uris.Lobby.CANCEL_QUEUE to SirenInfo(
            name = "cancel-queue",
            method = "POST",
            href = mediaType,
            type = "application/json",
            fields = listOf(),
            rel = listOf("cancel-queue"),
            title = "Cancel Queue"
    ),
    Uris.Game.LAYOUT_DEFINITION to SirenInfo(
            name = "place-ships",
            href = "http://localhost:8080/api/game/{gameId}/place-ships",
            method = "POST",
            type = mediaType,
            fields = listOf(),
            rel = listOf("place-ships"),
            title = "Place Ships"
    ),
    Uris.User.LOGIN to SirenInfo(
            name = "login",
            href = "http://localhost:8080/api/login",
            method = "POST",
            type = mediaType,
            fields = listOf(
                Field(name = "username", type = "text"),
                Field(name = "password", type = "password")
            ),
            rel = listOf("login"),
            title = "Login"
    ),
    Uris.User.REGISTER to SirenInfo(
            name = "register",
            href = "http://localhost:8080/api/user",
            method = "POST",
            type = mediaType,
            fields = listOf(
                Field(name = "username", type = "text"),
                Field(name = "password", type = "password")
            ),
            rel = listOf("register"),
            title = "Register"
    ),
    Uris.Game.SHOTS_DEFINITION to SirenInfo(
            name = "shots",
            href = "http://localhost:8080/api/game/{gameId}/shots",
            method = "POST",
            type = mediaType,
            fields = listOf(

            ),
            rel = listOf("shots"),
            title = "Shots"
    ),
    Uris.Game.GAME_STATE to SirenInfo(
            name = "game-state",
            href = "http://localhost:8080/api/game/{gameId}/state",
            method = "GET",
            type = mediaType,
            fields = listOf(),
            rel = listOf("game-state"),
            title = "Game State"
    ),
    Uris.Game.MY_FLEET to SirenInfo(
            name = "my-fleet",
            href = "http://localhost:8080/api/game/{gameId}/my-fleet",
            method = "GET",
            type = mediaType,
            fields = listOf(),
            rel = listOf("my-fleet"),
            title = "My Fleet"
    ),
    Uris.Game.OPPONENT_FLEET to SirenInfo(
            name = "opponent-fleet",
            href = "http://localhost:8080/api/game/{gameId}/opponent-fleet",
            method = "GET",
            type = mediaType,
            fields = listOf(),
            rel = listOf("opponent-fleet"),
            title = "Opponent Fleet"
    ),
    Uris.SYSTEM_INFO to SirenInfo(
            name = "system-info",
            href = "http://localhost:8080/api/system-info",
            method = "GET",
            type = mediaType,
            fields = listOf(),
            rel = listOf("system-info"),
            title = "System Info"
    ),
    Uris.STATISTICS to SirenInfo(
            name = "statistics",
            href = "http://localhost:8080/api/statistics",
            method = "GET",
            type = mediaType,
            fields = listOf(),
            rel = listOf("statistics"),
            title = "Statistics"
    )
)

private fun updateHref(uri: String, uriVariables: Map<String, String>?): String {
    if (uriVariables.isNullOrEmpty()) return uri
    val template = UriTemplate(uri)
    return template.expand(uriVariables).toString()
}


private fun get(linking: Linking, uri: String, uriVariables: Map<String, String>?): List<SirenInfo>{
    val uriRelationsMap = if (linking == Linking.ACTIONS) UriActionsRelationsMap else UriLinksRelationsMap

    return uriRelationsMap[uri]?.map { rel ->
        val sirenInfo = SirenInfoMap[rel] ?: throw IllegalArgumentException("No sirenInfo found for $rel")
        val href = updateHref(sirenInfo.href, uriVariables)
        sirenInfo.copy(href = href)
    } ?: emptyList()
}

private fun getTitle(uri: String) =
    SirenInfoMap[uri]?.title ?: throw IllegalArgumentException("No action found for $uri")

fun <T> T.toSiren(uri: String, uriVariables: Map<String, String>? = null): SirenEntity<T> {
    val actions = get(Linking.ACTIONS, uri, uriVariables).map { it.toAction() }
    val links = get(Linking.LINKS, uri, uriVariables).map { it.toLink() } + listOf(selfLink(updateHref(uri, uriVariables)))

    return SirenEntity(
        title = getTitle(uri),
        properties = this,
        actions = actions,
        links = links,
        entities = listOf()
    )
}

fun noEntitySiren(uri: String, uriVariables: Map<String, String>? = null): SirenEntity<Nothing> {
    val actions = get(Linking.ACTIONS, uri, uriVariables).map { it.toAction() }
    val links = get(Linking.LINKS, uri, uriVariables).map { it.toLink() } + listOf(selfLink(updateHref(uri, uriVariables)))

    return SirenEntity(
        title = getTitle(uri),
        actions = actions,
        links = links,
        entities = listOf()
    )
}

enum class Linking {
    ACTIONS, LINKS
}
