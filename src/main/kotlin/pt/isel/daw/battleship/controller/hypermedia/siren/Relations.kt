package pt.isel.daw.battleship.controller.hypermedia.siren

import org.springframework.web.util.UriTemplate
import pt.isel.daw.battleship.controller.Method
import pt.isel.daw.battleship.controller.MethodInfo
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenAction.*


private val root = MethodInfo(Uris.Home.ROOT, Method.POST)
private val queue = MethodInfo(Uris.Lobby.QUEUE, Method.POST)
private val cancelQueue = MethodInfo(Uris.Lobby.CANCEL_QUEUE, Method.POST)
private val login = MethodInfo(Uris.User.LOGIN, Method.POST)
private val register = MethodInfo(Uris.User.REGISTER, Method.POST)
private val layoutDefinition = MethodInfo(Uris.Game.LAYOUT_DEFINITION, Method.POST)
private val opponentFleet = MethodInfo(Uris.Game.OPPONENT_FLEET, Method.GET)
private val shotsDefinition = MethodInfo(Uris.Game.SHOTS_DEFINITION, Method.POST)
private val systemInfo = MethodInfo(Uris.Home.SYSTEM_INFO, Method.GET)
private val statistics = MethodInfo(Uris.Home.STATISTICS, Method.GET)
private val gameState = MethodInfo(Uris.Game.GAME_STATE, Method.GET)
private val myFleet = MethodInfo(Uris.Game.MY_FLEET, Method.GET)


private const val mediaType = "application/json"

private val UriActionsRelationsMap = mutableMapOf<MethodInfo, List<MethodInfo>>(
    queue to listOf(
        cancelQueue, layoutDefinition
    ),
    cancelQueue to listOf(
        queue
    ),
    register to listOf(
        login
    ),
    login to listOf(
        register
    ),
    layoutDefinition to listOf(
        shotsDefinition
    ),
    opponentFleet to listOf(
        shotsDefinition
    ),
    systemInfo to listOf(
        login, register
    ),
    statistics to listOf(
        login, register
    ),
)

private val UriLinksRelationsMap = mutableMapOf<MethodInfo, List<MethodInfo>>(
    root to listOf(
        systemInfo, statistics
    ),
    queue to listOf(
        root
    ),
    cancelQueue to listOf(
        root
    ),
    login to listOf(
        root
    ),
    register to listOf(
        root
    ),
    layoutDefinition to listOf(
        root, gameState, myFleet, opponentFleet
    ),
    opponentFleet to listOf(
        root, gameState, myFleet
    ),
    myFleet to listOf(
        root, gameState, opponentFleet
    ),
    shotsDefinition to listOf(
        root, gameState, opponentFleet
    ),
    systemInfo to listOf(
        root
    ),
    statistics to listOf(
        root
    ),
)

private val SirenInfoMap = mutableMapOf<MethodInfo, SirenInfo>(
    root to SirenInfo(
        name = "home",
        href = root.uri,
        method = root.method.name,
        type = mediaType,
        fields = listOf(),
        rel = listOf("home"),
        title = "Home"
    ),
    queue to SirenInfo(
        name = "play-intent",
        href = queue.uri,
        method = queue.method.name,
        type = mediaType,
        fields = listOf(),
        rel = listOf("play-intent"),
        title = "Play Intent"
    ),
    cancelQueue to SirenInfo(
        name = "cancel-queue",
        method = cancelQueue.method.name,
        href = cancelQueue.uri,
        type = mediaType,
        fields = listOf(),
        rel = listOf("cancel-queue"),
        title = "Cancel Queue"
    ),
    layoutDefinition to SirenInfo(
        name = "place-ships",
        href = layoutDefinition.uri,
        method = layoutDefinition.method.name,
        type = mediaType,
        fields = listOf(
            ListField(
                name = "shipInfo",
                type = listOf(
                    listOf(
                        ListField(
                            name = "initialSquare",
                            type = listOf(
                                Field("row", "number"),
                                Field("column", "number")
                            )
                        ),
                        Field(
                            name = "size",
                            type = "number"
                        ),
                        Field(
                            name = "orientation",
                            type = "string"
                        )
                    )
                )
            ),
        ),
        rel = listOf("place-ships"),
        title = "Place Ships"
    ),
    login to SirenInfo(
        name = "login",
        href = login.uri,
        method = login.method.name,
        type = mediaType,
        fields = listOf(
            Field(name = "username", type = "text"),
            Field(name = "password", type = "password")
        ),
        rel = listOf("login"),
        title = "Login"
    ),
    register to SirenInfo(
        name = "register",
        href = register.uri,
        method = register.method.name,
        type = mediaType,
        fields = listOf(
            Field(name = "username", type = "text"),
            Field(name = "password", type = "password")
        ),
        rel = listOf("register"),
        title = "Register"
    ),
    shotsDefinition to SirenInfo(
        name = "shots",
        href = shotsDefinition.uri,
        method = shotsDefinition.method.name,
        type = mediaType,
        fields = listOf(
            ListField(
                name = "shots",
                type = listOf(
                    Field("row", "number"),
                    Field("column", "number")
                )
            )
        ),
        rel = listOf("shots"),
        title = "Shots"
    ),
    gameState to SirenInfo(
        name = "game-state",
        href = gameState.uri,
        method = gameState.method.name,
        type = mediaType,
        fields = listOf(),
        rel = listOf("game-state"),
        title = "Game State"
    ),
    myFleet to SirenInfo(
        name = "my-fleet",
        href = myFleet.uri,
        method = myFleet.method.name,
        type = mediaType,
        fields = listOf(),
        rel = listOf("my-fleet"),
        title = "My Fleet"
    ),
    opponentFleet to SirenInfo(
        name = "opponent-fleet",
        href = opponentFleet.uri,
        method = opponentFleet.method.name,
        type = mediaType,
        fields = listOf(),
        rel = listOf("opponent-fleet"),
        title = "Opponent Fleet"
    ),
    systemInfo to SirenInfo(
        name = "system-info",
        href = systemInfo.uri,
        method = systemInfo.method.name,
        type = mediaType,
        fields = listOf(),
        rel = listOf("system-info"),
        title = "System Info"
    ),
    statistics to SirenInfo(
        name = "statistics",
        href = statistics.uri,
        method = statistics.method.name,
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

private fun get(linking: Linking, methodInfo: MethodInfo, uriVariables: Map<String, String>?): List<SirenInfo> {
    val uriMethodInfoMap = if (linking == Linking.ACTIONS) UriActionsRelationsMap else UriLinksRelationsMap
    return uriMethodInfoMap[methodInfo]?.map { rel ->
        val sirenInfo = SirenInfoMap[rel] ?: throw IllegalArgumentException("No sirenInfo found for $rel")
        val href = updateHref(sirenInfo.href, uriVariables)
        sirenInfo.copy(href = href)
    } ?: emptyList()
}

private fun getTitle(methodInfo: MethodInfo): String {
    println(methodInfo)
    return SirenInfoMap[methodInfo]?.title ?: throw IllegalArgumentException("No action found for $methodInfo")
}


fun <T> T.toSiren(methodInfo: MethodInfo, uriVariables: Map<String, String>? = null): SirenEntity<T> {
    val actions = get(Linking.ACTIONS, methodInfo, uriVariables).map { it.toAction() }
    val links =
        get(
            Linking.LINKS,
            methodInfo,
            uriVariables
        ).map { it.toLink() } + listOf(selfLink(updateHref(methodInfo.uri, uriVariables)))

    return SirenEntity(
        title = getTitle(methodInfo),
        properties = this,
        actions = actions,
        links = links,
        entities = listOf()
    )
}

fun noEntitySiren(methodInfo: MethodInfo, uriVariables: Map<String, String>? = null): SirenEntity<Nothing> {
    val actions = get(Linking.ACTIONS, methodInfo, uriVariables).map { it.toAction() }
    val links =
        get(
            Linking.LINKS,
            methodInfo,
            uriVariables
        ).map { it.toLink() } + listOf(selfLink(updateHref(methodInfo.uri, uriVariables)))

    return SirenEntity(
        title = getTitle(methodInfo),
        actions = actions,
        links = links,
        entities = listOf()
    )
}




