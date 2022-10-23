package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import pt.isel.daw.battleship.repository.GeneralRepository
import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.PlayerStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo

class JdbiGeneralRepository(
    private val handle: Handle
) : GeneralRepository {

    /**
     * Gets information about the system
     */
    override fun getSystemInfo(): SystemInfo {
        //TODO: make a mapper for this
        val authors = handle.createQuery("SELECT * FROM authors")
            .mapTo<SystemInfo.Author>()
            .list()

        val sysVersion = handle.createQuery("SELECT version from systeminfo")
            .mapTo<String>()
            .first()

        return SystemInfo(authors, sysVersion)
    }

    /**
     * Gets the game statistics
     */
    override fun getStatistics(): GameStatistics {

        val numGames = handle.createQuery("SELECT COUNT(*) FROM game")
            .mapTo<Int>()
            .one()

        val ranking = handle.createQuery("""SELECT "User".name, COUNT(*) as gamesWon FROM "User" JOIN game ON "User".id = game.winner""" +
                """ GROUP BY "User".name ORDER BY gamesWon DESC""")
            .mapTo<PlayerStatistics>()
            .toList()

        return GameStatistics(numGames, ranking)
    }

}

//TODO: remove
data class GameStatisticsDTO(
    val numGames: Int,
    @Nested val ranking: List<PlayerStatistics>
)