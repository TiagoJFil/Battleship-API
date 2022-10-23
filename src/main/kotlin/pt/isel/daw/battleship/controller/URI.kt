package pt.isel.daw.battleship.controller

import org.springframework.web.util.UriTemplate


object Uris {

    const val API = "/api"
    const val HOME = "$API/"
    const val SYSTEM_INFO = "systemInfo"
    const val STATISTICS = "statistics"

    object User {
        const val ROOT = "$API/user"
        const val LOGIN = "/login"
    }

    object Lobby {
        const val ROOT = "$API/lobby"
        const val LOBBY = "/"
        const val DELETE_LOBBY = "/cancel"
    }

    object Game {
        const val ROOT = "$API/game/"

        const val LAYOUT_DEFINITION = "{gameId}/layoutDefinition"
        const val SHOTS_DEFINITION = "{gameId}/shotsDefinition"
        const val MY_FLEET = "{gameId}/myFleet"
        const val OPPONENT_FLEET = "{gameId}/opponentFleet"
        const val GAME_STATE = "{gameId}/gameState"
    }



}


