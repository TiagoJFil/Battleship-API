package pt.isel.daw.battleship.controller


object Uris {

    object Home {
        const val ROOT = "/"
        const val SYSTEM_INFO = "${ROOT}systemInfo"
        const val STATISTICS = "${ROOT}statistics"
    }


    object User {
        private const val ROOT = "/user"
        const val LOGIN = "$ROOT/login"
        const val REGISTER = ROOT
    }

    object Lobby {
        private const val ROOT = "/lobby"
        const val QUEUE = "$ROOT/"
        const val CANCEL_QUEUE = "$ROOT/cancel"
        const val STATE = "$ROOT/{lobbyId}"
    }

    object Game {
        private const val ROOT = "/game/"
        private const val GAME_ID_PLACEHOLDER = "{gameId}"
        private const val GAME_ID_URI = "$ROOT$GAME_ID_PLACEHOLDER"

        const val LAYOUT_DEFINITION = "$GAME_ID_URI/layoutDefinition"
        const val SHOTS_DEFINITION = "$GAME_ID_URI/shotsDefinition"
        const val MY_FLEET = "$GAME_ID_URI/myFleet"
        const val OPPONENT_FLEET = "$GAME_ID_URI/opponentFleet"
        const val GAME_STATE = "$GAME_ID_URI/state"
    }



}