package pt.isel.daw.battleship.controller


object Uris {

    object Home {
        const val ROOT = "/"
        const val SYSTEM_INFO = "${ROOT}systemInfo"
        const val STATISTICS = "${ROOT}statistics"
    }


    object User {
        private const val ROOT = "/user"
        const val HOME = "/my"
        const val LOGIN = "$ROOT/login"
        const val REGISTER = ROOT
        const val GET_USER = "$ROOT/{id}"
    }

    object Lobby {
        private const val ROOT = "/lobby"
        const val QUEUE = ROOT
        const val CANCEL_QUEUE = "$ROOT/cancel"
        const val STATE = "$ROOT/{lobbyId}"
    }

    object Game {
        private const val ROOT = "/game"
        private const val GAME_ID_PLACEHOLDER = "/{gameId}"
        private const val GAME_ID_URI = "$ROOT$GAME_ID_PLACEHOLDER"
        const val FLEET = "$GAME_ID_URI/fleet/{whichFleet}"
        const val STATE = "$GAME_ID_URI/state"
        const val LAYOUT_DEFINITION = "$GAME_ID_URI/layout-definition"
        const val SHOTS_DEFINITION = "$GAME_ID_URI/shots-definition"
        const val WHICH_FLEET_PLACEHOLDER = "{whichFleet}"
    }

}