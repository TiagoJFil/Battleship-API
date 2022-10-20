package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.model.Game
import pt.isel.daw.battleship.model.Id
import pt.isel.daw.battleship.repository.dto.GameDTO

interface GameRepository {
    /**
     * Gets the given game by its id.
     * @param gameID
     * @return [Game]
     */
    fun getGame(gameID: Id): Game?

    /**
     * Gets a game in waiting state.
     */
    fun getWaitingStateGame(): Game?

    /**
     *
     */
    fun persist(game: GameDTO): Id?
}