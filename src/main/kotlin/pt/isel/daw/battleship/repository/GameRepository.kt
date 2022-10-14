package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.model.Id
import pt.isel.daw.battleship.model.Game
import pt.isel.daw.battleship.services.entities.User

interface GameRepository {

    fun getNumOfGames(): Int

    /**
     * Gets the [Game.State] of a game and the Winner id if the state is [Game.State.FINISHED]
     * @param gameId the id of the game
     */
    fun getGameState(gameId: Id): Pair<Game.State, User?>


    /**
     * Gets all the games
     */
    fun getGames(): List<Game>

    /**
     * Gets the [Game] with the given id
     */
    fun getGame(gameId: Id): Game?

    /**
     * Updates a [Game]
     */
    fun updateGame(gameId: Id, game: Game)

    /**
     * Verifies if the game with the given id exists
     */
    fun hasGame(gameId: Id): Boolean

    /**
     * Verifies wheter the userId received is on its turn to play
     * @return TRUE if the user is on its turn, FALSE otherwise
     */
    fun verifyTurn(userId: Id, gameId: Id) : Boolean
}