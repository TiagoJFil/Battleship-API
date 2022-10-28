package pt.isel.daw.battleship.controller


object Uris {


    const val HOME = "/"
    const val SYSTEM_INFO = "systeminfo"
    const val STATISTICS = "statistics"

    object User {
        const val ROOT = "/user"
        const val LOGIN = "/login"
        const val REGISTER = "/user"
    }

    object Lobby {
        const val ROOT = "/lobby"
        const val QUEUE = "/"
        const val CANCEL_QUEUE = "/cancel"
    }

    object Game {
        const val ROOT = "/game/"

        const val LAYOUT_DEFINITION = "{gameId}/layoutDefinition"
        const val SHOTS_DEFINITION = "{gameId}/shotsDefinition"
        const val MY_FLEET = "{gameId}/myFleet"
        const val OPPONENT_FLEET = "{gameId}/opponentFleet"
        const val GAME_STATE = "{gameId}/gameState"
    }



}


