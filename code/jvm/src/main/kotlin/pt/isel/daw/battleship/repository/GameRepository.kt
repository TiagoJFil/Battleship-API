package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.repository.dto.*
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID

interface GameRepository {
    /**
     * Gets the game with the given id
     * @param gameID the [ID] of the game
     * @return [Game] the game
     */
    fun get(gameID: ID): Game?

    /**
     * Persists the given game in the database
     * @param game the game to be persisted
     * @return [ID] of the game persisted
     */
    fun persist(game: GameDTO): ID

    /**
     * Gets all the games that the given user is participating in and that are not finished
     * @param userID the [UserID] of the user
     */
    fun getUserGames(userID: UserID): List<ID>

}