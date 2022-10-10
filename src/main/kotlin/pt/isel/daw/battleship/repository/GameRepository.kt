package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.data.model.Id
import pt.isel.daw.battleship.data.model.Game

interface GameRepository {
    fun getGames(): List<Game>
    fun getNumOfGames(): Int
    fun getGame(gameId: Id): Game?
    fun updateGame(gameId: Id, game: Game)
}