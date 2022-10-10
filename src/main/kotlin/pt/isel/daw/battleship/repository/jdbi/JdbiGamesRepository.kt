package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.data.model.Id
import pt.isel.daw.battleship.data.model.Game
import pt.isel.daw.battleship.repository.GameRepository

class JdbiGamesRepository(
    private val handle: Handle
) : GameRepository {
    override fun getGames(): List<Game> {
        TODO("Not yet implemented")
    }

    override fun getNumOfGames(): Int {
        TODO("Not yet implemented")
    }

    override fun getGame(gameId: Id): Game? {
        TODO("Not yet implemented")
    }

    override fun updateGame(gameId: Id, game: Game) {
        TODO("Not yet implemented")
    }
}