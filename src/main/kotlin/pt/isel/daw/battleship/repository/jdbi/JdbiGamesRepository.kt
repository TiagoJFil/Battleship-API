package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.battleship.data.model.Id
import pt.isel.daw.battleship.data.model.Game
import pt.isel.daw.battleship.repository.GameRepository


class JdbiGamesRepository(
    private val handle: Handle
) : GameRepository {
    override fun getGames(): List<Game> {
        TODO("Not yet implemented")
    }

    /**
     * Returns the total number of games in the repository.
     */
    override fun getNumOfGames(): Int =
        handle.createQuery("SELECT COUNT(*) FROM game")
            .mapTo<Int>()
            .one()

    //n funfa ainda
    override fun getGame(gameId: Id): Game? {
        return handle.createQuery("SELECT * FROM game WHERE id = :id")
            .bind("id", gameId)
            .mapTo<Game>()
            .singleOrNull()
            ?.run {
                toGame()
            }
    }

    override fun updateGame(gameId: Id, game: Game) {
        TODO("Not yet implemented")
    }
}

