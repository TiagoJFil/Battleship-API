package pt.isel.daw.battleship.data

import pt.isel.daw.battleship.data.model.Game

interface GameRepository {

    fun getGames(): List<Game>

    fun getGame(gameId: Id): Game?
}