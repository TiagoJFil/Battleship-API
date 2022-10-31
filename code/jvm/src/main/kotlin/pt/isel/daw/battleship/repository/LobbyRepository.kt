package pt.isel.daw.battleship.repository;

import pt.isel.daw.battleship.utils.UserID

interface LobbyRepository {
    /**
     * Gets a player that is waiting in the lobby or null if there is none.
     * @return [UserID]
     */
    fun getWaitingPlayer(): UserID?

    /**
     * Adds a player to the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean]
     */
    fun addPlayerToLobby(userID: UserID): Boolean

    /**
     * Removes a player from the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean]
     */
    fun removePlayerFromLobby(userID: UserID): Boolean
}
