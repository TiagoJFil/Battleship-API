package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.battleship.model.Id
import pt.isel.daw.battleship.model.Game
import pt.isel.daw.battleship.model.Game.*
import pt.isel.daw.battleship.model.GameRules
import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.services.dto.GameDBTO
import pt.isel.daw.battleship.utils.UserID


class JdbiGamesRepository(
    private val handle: Handle
) : GameRepository {

    override fun getGames(): List<Game> {
        TODO("Not yet implemented")
    }

    override fun getGame(gameId: Id): GameDBTO? {
        return handle.createQuery("""SELECT * FROM game WHERE id = :gameId""")
            .bind("gameId", gameId)
            .mapTo<GameDBTO>()
            .firstOrNull()
    }

    /**
     * Returns the total number of games in the repository.
     */
    override fun getNumOfGames(): Int =
        handle.createQuery("SELECT COUNT(*) FROM game")
            .mapTo<Int>()
            .one()

    override fun getGameState(gameId: Id): Pair<State, User?> {
        return handle.createQuery("""SELECT "state", u.id as userId, u.name as userName FROM game g join "User" u on g.winner = u.id WHERE g.id = :id""")
                .bind("id", gameId)
                .map{ rs, _ ->
                    val state = State.valueOf(rs.getString("State").uppercase())
                    val winner = if(state == State.FINISHED) User(rs.getInt("userid"), rs.getString("username")) else null
                    return@map Pair(state, winner)
                }.one()
    }

    override fun updateGame(gameId: Id, game: Game) {
        TODO("Not yet implemented")
    }

    override fun hasGame(gameId: Id): Boolean {
        return handle.createQuery("""SELECT COUNT(*) FROM game WHERE id = :gameId""")
            .bind("gameId", gameId)
            .mapTo<Int>()
            .one() == 1
    }

    override fun verifyTurn(userId: Id, gameId: Id): Boolean {
        return true
    }
}

