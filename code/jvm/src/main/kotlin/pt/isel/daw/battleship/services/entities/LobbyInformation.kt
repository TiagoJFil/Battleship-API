package pt.isel.daw.battleship.services.entities

import pt.isel.daw.battleship.utils.ID

data class LobbyInformation(
    val lobbyID: ID,
    val gameID: ID?
)
