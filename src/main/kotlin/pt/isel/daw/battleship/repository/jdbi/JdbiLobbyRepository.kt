package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.LobbyRepository
import pt.isel.daw.battleship.utils.UserID

class JdbiLobbyRepository(
    private val handle: Handle
): LobbyRepository {
    /**
     * Gets a player that is waiting in the lobby or null if there is none.
     * @return [UserID]
     */
    override fun getWaitingPlayer(): UserID? {
        return handle.createQuery("SELECT userID from waitinglobby")
            .mapTo(Int::class.java)
            .firstOrNull()
    }

    /**
     * Adds a player to the waiting lobby.
     * @param userID The player's ID.
     */
    override fun addPlayerToLobby(userID: UserID): Boolean {
        return handle.createUpdate("INSERT INTO waitinglobby VALUES (:userID)")
            .bind("userID", userID)
            .execute() == 1
    }

    /**
     * Removes a player from the waiting lobby.
     * @param userID The player's ID.
     * @return [Boolean]
     */
    override fun removePlayerFromLobby(userID: UserID): Boolean {
        return handle.createUpdate("DELETE FROM waitinglobby WHERE id = " +
                "(SELECT min(id) FROM waitinglobby WHERE userID = :userID)")
            .bind("userID", userID)
            .execute() == 1
    }
}