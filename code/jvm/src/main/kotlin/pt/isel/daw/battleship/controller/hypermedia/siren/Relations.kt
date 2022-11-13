package pt.isel.daw.battleship.controller.hypermedia.siren

import org.springframework.web.util.UriTemplate
import pt.isel.daw.battleship.controller.Method
import pt.isel.daw.battleship.controller.MethodInfo
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.cancelQueue
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.gameState
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.layoutDefinition
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.lobbyState
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.login
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.myFleet
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.opponentFleet
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.queue
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.register
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.root
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.shotsDefinition
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.statistics
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData.systemInfo
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenAction.*



object AppEndpointsMetaData{

    val root = MethodInfo(Uris.Home.ROOT, Method.GET)
    val queue = MethodInfo(Uris.Lobby.QUEUE, Method.POST)
    val cancelQueue = MethodInfo(Uris.Lobby.CANCEL_QUEUE, Method.POST)
    val login = MethodInfo(Uris.User.LOGIN, Method.POST)
    val register = MethodInfo(Uris.User.REGISTER, Method.POST)
    val layoutDefinition = MethodInfo(Uris.Game.LAYOUT_DEFINITION, Method.POST)
    val opponentFleet = MethodInfo(Uris.Game.OPPONENT_FLEET, Method.GET)
    val shotsDefinition = MethodInfo(Uris.Game.SHOTS_DEFINITION, Method.POST)
    val systemInfo = MethodInfo(Uris.Home.SYSTEM_INFO, Method.GET)
    val statistics = MethodInfo(Uris.Home.STATISTICS, Method.GET)
    val gameState = MethodInfo(Uris.Game.GAME_STATE, Method.GET)
    val myFleet = MethodInfo(Uris.Game.MY_FLEET, Method.GET)
    val lobbyState = MethodInfo(Uris.Lobby.STATE, Method.GET)
}

private const val JsonContentType = "application/json"

private val UriActionsRelationsMap = mutableMapOf<MethodInfo, List<MethodInfo>>(
    root to listOf(
        register, login
    ),
    queue to listOf(
        cancelQueue, layoutDefinition,
    ),
    cancelQueue to listOf(
        queue
    ),
    register to listOf(
        queue
    ),
    login to listOf(
        queue
    ),
    layoutDefinition to listOf(
        shotsDefinition,
    ),
    opponentFleet to listOf(
        shotsDefinition
    ),
    myFleet to listOf(
        shotsDefinition
    ),
    gameState to listOf(
        shotsDefinition
    ),
    shotsDefinition to listOf(),
    systemInfo to listOf(),
    statistics to listOf(),
    lobbyState to listOf(
        cancelQueue, layoutDefinition
    )
)

private val UriLinksRelationsMap = mutableMapOf<MethodInfo, List<MethodInfo>>(
    root to listOf(
        systemInfo, statistics
    ),
    queue to listOf(
        root, gameState, lobbyState
    ),
    cancelQueue to listOf(
    ),
    login to listOf(
        root
    ),
    register to listOf(
        root
    ),
    layoutDefinition to listOf(
        gameState, myFleet, opponentFleet
    ),
    opponentFleet to listOf(
        gameState, myFleet
    ),
    myFleet to listOf(
        gameState, opponentFleet
    ),
    gameState to listOf(
        myFleet, opponentFleet
    ),
    shotsDefinition to listOf(
        gameState, opponentFleet, myFleet
    ),
    systemInfo to listOf(
        root
    ),
    statistics to listOf(
        root
    ),
    lobbyState to listOf()
)

private val SirenInfoMap = mutableMapOf<MethodInfo, SirenInfo>(
    root to SirenInfo(
        name = "home",
        href = root.uri,
        method = root.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("home"),
        title = "Home"
    ),
    queue to SirenInfo(
        name = "play-intent",
        href = queue.uri,
        method = queue.method.name,
        outContentType = SirenContentType,
        inContentType = JsonContentType,
        fields = listOf(),
        rel = listOf("play-intent"),
        title = "Play Intent"
    ),
    cancelQueue to SirenInfo(
        name = "cancel-queue",
        method = cancelQueue.method.name,
        href = cancelQueue.uri,
        outContentType = SirenContentType,
        inContentType = JsonContentType,
        fields = listOf(),
        rel = listOf("cancel-queue"),
        title = "Cancel Queue"
    ),
    layoutDefinition to SirenInfo(
        name = "place-ships",
        href = layoutDefinition.uri,
        method = layoutDefinition.method.name,
        outContentType = SirenContentType,
        inContentType = JsonContentType,
        fields = listOf(
            Field(name = "shipInfoList", type= "shipinfolist")
        ),
        rel = listOf("place-ships"),
        title = "Place Ships"
    ),
    login to SirenInfo(
        name = "login",
        href = login.uri,
        method = login.method.name,
        outContentType = SirenContentType,
        inContentType = JsonContentType,
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
        outContentType = SirenContentType,
        inContentType = JsonContentType,
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
        outContentType = SirenContentType,
        inContentType = JsonContentType,
        fields = listOf(
           Field(name = "shotlist", type = "shotlist"),
        ),
        rel = listOf("shots"),
        title = "Shots"
    ),
    gameState to SirenInfo(
        name = "game-state",
        href = gameState.uri,
        method = gameState.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("game-state"),
        title = "Game State"
    ),
    myFleet to SirenInfo(
        name = "my-fleet",
        href = myFleet.uri,
        method = myFleet.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("my-fleet"),
        title = "My Fleet"
    ),
    opponentFleet to SirenInfo(
        name = "opponent-fleet",
        href = opponentFleet.uri,
        method = opponentFleet.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("opponent-fleet"),
        title = "Opponent Fleet"
    ),
    systemInfo to SirenInfo(
        name = "system-info",
        href = systemInfo.uri,
        method = systemInfo.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("system-info"),
        title = "System Info"
    ),
    statistics to SirenInfo(
        name = "statistics",
        href = statistics.uri,
        method = statistics.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("statistics"),
        title = "Statistics"
    ),
    lobbyState to SirenInfo(
        name = "lobby-state",
        href = lobbyState.uri,
        method = lobbyState.method.name,
        outContentType = SirenContentType,
        fields = listOf(),
        rel = listOf("lobby"),
        title = "Lobby State"
    )
)

fun updateHref(uri: String, uriVariables: Map<String, String?>?): String {
    if (uriVariables.isNullOrEmpty()) return uri
    val template = UriTemplate(uri)
    return template.expand(uriVariables).toString()
}

fun get(linking: Linking, methodInfo: MethodInfo, uriVariables: Map<String, String?>?): List<SirenInfo> {
    val uriMethodInfoMap = if (linking == Linking.ACTIONS) UriActionsRelationsMap else UriLinksRelationsMap

    return uriMethodInfoMap[methodInfo]?.map { rel ->
        val sirenInfo = SirenInfoMap[rel] ?: throw IllegalArgumentException("No sirenInfo found for $rel")
        val href = updateHref(sirenInfo.href, uriVariables?.filter { it.key in sirenInfo.href && it.value != null })
        sirenInfo.copy(href = href)
    } ?: emptyList()
}

fun getTitle(methodInfo: MethodInfo): String {
    println(methodInfo)
    return SirenInfoMap[methodInfo]?.title ?: throw IllegalArgumentException("No action found for $methodInfo")
}

// TODO: Adapt actions and links depending on uriVariables
inline fun <reified T> T.toSiren(methodInfo: MethodInfo, uriVariables: Map<String, String?>? = null): SirenEntity<T> {

    val actions = get(Linking.ACTIONS, methodInfo, uriVariables).map { it.toAction() }
    val links =
        get(
            Linking.LINKS,
            methodInfo,
            uriVariables
        ).map { it.toLink() } + listOf(selfLink(updateHref(methodInfo.uri, uriVariables)))

    return SirenEntity(
        clazz = listOf(T::class.java.simpleName),
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




