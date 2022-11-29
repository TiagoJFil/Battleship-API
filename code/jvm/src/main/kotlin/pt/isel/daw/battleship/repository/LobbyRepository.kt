package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.repository.dto.LobbyDTO
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID

interface LobbyRepository {

    /**
     * Adds a player to the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean] true if the update was successful, false otherwise.
     */
    fun createLobby(userID: UserID): ID

    /**
     * Sets the second joining player and the game ID to the waiting lobby with the given lobbyID.
     * @param lobbyID The lobby's ID.
     * @param player2 The second player's ID.
     * @param gameID The game's ID.
     * @return [Boolean] true if the update was successful, false otherwise.
     */
    fun completeLobby(lobbyID: ID, player2: UserID, gameID: ID): Boolean

    /**
     * Removes a player from the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean] true if the update was successful, false otherwise.
     */
    fun removePlayerFromLobby(userID: UserID): Boolean


    /**
     * Gets the first lobby in the waiting list where the given [UserID] is not present.
     * @param userID The player's ID.
     * @return [LobbyDTO]
     */
    fun findWaitingLobby(userID: UserID): LobbyDTO?


    /**
     * Gets the [LobbyDTO] with the given [ID].
     * @param lobbyID The lobby's ID.
     * @return [LobbyDTO]
     */
    fun get(lobbyID: ID): LobbyDTO?
}
