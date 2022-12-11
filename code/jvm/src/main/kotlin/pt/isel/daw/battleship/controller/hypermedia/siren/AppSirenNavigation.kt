package pt.isel.daw.battleship.controller.hypermedia.siren

import pt.isel.daw.battleship.controller.EndpointKeys.CANCEL_QUEUE_KEY
import pt.isel.daw.battleship.controller.EndpointKeys.LOGIN_KEY
import pt.isel.daw.battleship.controller.EndpointKeys.MY_FLEET_KEY
import pt.isel.daw.battleship.controller.EndpointKeys.OPPONENT_FLEET_KEY
import pt.isel.daw.battleship.controller.EndpointKeys.QUEUE_KEY
import pt.isel.daw.battleship.controller.EndpointKeys.REGISTER_KEY
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.Uris.Game.WHICH_FLEET_PLACEHOLDER
import pt.isel.daw.battleship.controller.dto.BoardDTO
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNodeID
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.buildSirenGraph
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.NoEntitySiren
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.GameRules
import pt.isel.daw.battleship.services.entities.*

inline fun <reified T : Any> T.appToSiren(
    nodeID: SirenNodeID,
    extraPlaceholders: Map<String, String?>? = null
): SirenEntity<T> = this.toSiren(AppSirenNavigation.graph, nodeID, extraPlaceholders)

inline fun <reified T  : Any,reified E : Any> SirenEntity<T>.appAppendEmbedded(
    embeddedNodeID: SirenNodeID,
    entity: E,
    originalNodeID : SirenNodeID,
    extraPlaceholders: Map<String, String?>? = null
): SirenEntity<T> = this.appendEmbedded(AppSirenNavigation.graph,embeddedNodeID, entity,originalNodeID,extraPlaceholders)

object AppSirenNavigation {

    const val ROOT_NODE_KEY = "home"
    const val LOBBY_STATE_NODE_KEY = "lobby-state"
    const val AUTH_INFO_NODE_KEY = "auth-info"
    const val USER_HOME_NODE_KEY = "user-home"
    const val USER_NODE_KEY = "user"
    const val FLEET_NODE_KEY = "fleet"
    const val DEFINE_LAYOUT_NODE_ID = "layout-definition"
    const val SHOTS_DEFINITION_NODE_KEY = "shots-definition"
    const val GAME_STATE_NODE_KEY = "game-state"
    const val GAME_RULES_NODE_KEY = "game-rules"
    const val STATISTICS_NODE_KEY = "statistics"
    const val SYSTEM_INFO_NODE_KEY = "system-info"

    val graph = buildSirenGraph {

        node<NoEntitySiren>(ROOT_NODE_KEY) {
            self(Uris.User.REGISTER)
            link(listOf(STATISTICS_NODE_KEY), Uris.Home.STATISTICS)
            link(listOf(SYSTEM_INFO_NODE_KEY), Uris.Home.SYSTEM_INFO)

            action(LOGIN_KEY, Uris.User.LOGIN, "POST") {
                field("username", type = "text")
                field("password", type = "password")
            }

            action(REGISTER_KEY, Uris.User.REGISTER, "POST") {
                field("username", type = "text")
                field("password", type = "password")
            }
        }

        node<Statistics>(STATISTICS_NODE_KEY) {
            self(Uris.Home.STATISTICS)
            link(listOf(ROOT_NODE_KEY), Uris.Home.ROOT)
            link(listOf(USER_NODE_KEY), Uris.User.GET_USER)
            embeddedEntity<User>(
                rel = listOf("user associated to id"),
            )
        }

        node<User>(USER_NODE_KEY) {
            self(Uris.User.GET_USER)
        }

        node<SystemInfo>(SYSTEM_INFO_NODE_KEY) {
            self(Uris.Home.SYSTEM_INFO)
            link(listOf(ROOT_NODE_KEY), Uris.Home.ROOT)
        }

        node<AuthInformation>(AUTH_INFO_NODE_KEY) {
            link(listOf(ROOT_NODE_KEY), Uris.Home.ROOT)
        }

        node<NoEntitySiren>(USER_HOME_NODE_KEY) {
            self(Uris.User.HOME)
            action(
                name = QUEUE_KEY,
                href = Uris.Lobby.QUEUE,
                method = "POST",
                title = "Play Intent"
            )
        }

        node<LobbyInformation>(LOBBY_STATE_NODE_KEY) {
            self(href = Uris.Lobby.STATE)
            link(listOf(USER_HOME_NODE_KEY), Uris.User.HOME)
            link(listOf(GAME_STATE_NODE_KEY), Uris.Game.STATE)
            action(CANCEL_QUEUE_KEY, Uris.Lobby.CANCEL_QUEUE, "DELETE", title = "Cancel")
            link(listOf(USER_NODE_KEY), Uris.User.GET_USER)
            link(listOf(GAME_RULES_NODE_KEY), Uris.Game.RULES) showWhen { it.gameID != null }
        }

        node<GameStateInfo>(GAME_STATE_NODE_KEY) {
            self(Uris.Game.STATE)
            action(
                name = SHOTS_DEFINITION_NODE_KEY,
                href = Uris.Game.SHOTS_DEFINITION,
                "POST",
                title = "Make Play"
            ) showWhen { it.state == Game.State.PLAYING }
            action(
                name = DEFINE_LAYOUT_NODE_ID,
                href = Uris.Game.LAYOUT_DEFINITION,
                "POST",
                title = "Place Ships"
            ) showWhen { it.state == Game.State.PLACING_SHIPS }
            embeddedLink(
                clazz = listOf(FLEET_NODE_KEY),
                href = Uris.Game.FLEET.replace(WHICH_FLEET_PLACEHOLDER, "my"),
                rel = listOf(MY_FLEET_KEY),
                title = "My Fleet"
            ) showWhen { it.state == Game.State.PLAYING || it.state == Game.State.FINISHED }
            embeddedLink(
                clazz = listOf(FLEET_NODE_KEY),
                href = Uris.Game.FLEET.replace(WHICH_FLEET_PLACEHOLDER, "opponent"),
                rel = listOf(OPPONENT_FLEET_KEY),
                title = "Opponent's Fleet"
            ) showWhen { it.state == Game.State.PLAYING || it.state == Game.State.FINISHED }
            link(listOf(GAME_RULES_NODE_KEY), Uris.Game.RULES)
        }


        node<NoEntitySiren>(nodeID = DEFINE_LAYOUT_NODE_ID){
            self(Uris.Game.LAYOUT_DEFINITION)
            link(listOf(GAME_RULES_NODE_KEY), Uris.Game.RULES)
        }
        node<NoEntitySiren>(nodeID = SHOTS_DEFINITION_NODE_KEY){
            self(Uris.Game.SHOTS_DEFINITION)

            embeddedEntity<BoardDTO>(
                rel = listOf("board"),
            )
        }

        node<BoardDTO>(FLEET_NODE_KEY) {
            link(listOf(GAME_STATE_NODE_KEY), Uris.Game.STATE)
        }

        node<GameRules>(GAME_RULES_NODE_KEY)
    }
}






