package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.repository.dto.LobbyDTO
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID

interface LobbyRepository {

    /**
     * Adds a player to the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean]
     */
    fun createLobby(userID: UserID): ID

    /**
     *
     */
    fun completeLobby(lobbyID: ID, player2: UserID, gameID: ID): Boolean

    /**
     * Removes a player from the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean]
     */
    fun removePlayerFromLobby(userID: UserID): Boolean


    fun findWaitingLobby(): LobbyDTO?


    fun get(lobbyId: ID): LobbyDTO?
}
