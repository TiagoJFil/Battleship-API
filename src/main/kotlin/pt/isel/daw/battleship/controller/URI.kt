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
    }

    object Game {
        private const val ROOT = "/game/"

        const val LAYOUT_DEFINITION = "$ROOT{gameId}/layoutDefinition"
        const val SHOTS_DEFINITION = "$ROOT{gameId}/shotsDefinition"
        const val MY_FLEET = "$ROOT{gameId}/myFleet"
        const val OPPONENT_FLEET = "$ROOT{gameId}/opponentFleet"
        const val GAME_STATE = "$ROOT{gameId}/state"
    }



}