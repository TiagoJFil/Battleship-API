package pt.isel.daw.battleship.repository.fake

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.data.model.Id
import pt.isel.daw.battleship.data.model.Game
import pt.isel.daw.battleship.repository.GameRepository

class FakeGameRepo(handle: Handle) : GameRepository {

    private val table = mutableMapOf<Id, Game>()

    override fun getGames(): List<Game> = table.values.toList()

    override fun getNumOfGames(): Int {
        TODO("Not yet implemented")
    }

    fun addGame(game: Game){
        table[game.Id] = game
    }

    fun hasGame(gameId: Id) = table[gameId] != null

    override fun getGame(gameId: Id): Game? = table[gameId]
    override fun updateGame(gameId: Id, game: Game) {
        table[gameId] = game
    }

}