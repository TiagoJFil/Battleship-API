package pt.isel.daw.battleship.controller


object Uris {

    object Home {
        const val ROOT = "/"
        const val SYSTEM_INFO = "/systemInfo"
        const val STATISTICS = "/statistics"
    }

    object User {
        private const val ROOT = "/user"
        const val HOME = "/my"
        const val LOGIN = "$ROOT/login"
        const val REGISTER = ROOT
        const val GET_USER = "$ROOT/{id}"
        const val GAMES = "/games"
    }

    object Lobby {
        const val ROOT = "/lobby"
        const val QUEUE = ROOT
        const val CANCEL_QUEUE = "$ROOT/{lobbyID}"
        const val STATE = "$ROOT/{lobbyID}"
    }

    object Game {
        const val ROOT = "/game"
        private const val GAME_ID_PLACEHOLDER = "/{gameID}"
        private const val RESOURCE = "$ROOT$GAME_ID_PLACEHOLDER"
        const val FLEET = "$RESOURCE/fleet/{whichFleet}"
        const val STATE = "$RESOURCE/state"
        const val RULES = "$RESOURCE/rules"
        const val LAYOUT_DEFINITION = "$RESOURCE/layout-definition"
        const val SHOTS_DEFINITION = "$RESOURCE/shots-definition"
        const val WHICH_FLEET_PLACEHOLDER = "{whichFleet}"
    }

}