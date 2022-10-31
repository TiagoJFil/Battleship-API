package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import pt.isel.daw.battleship.repository.GeneralRepository
import pt.isel.daw.battleship.repository.dto.PlayerStatisticDTO
import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.PlayerStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo
import pt.isel.daw.battleship.utils.ID

class JdbiGeneralRepository(
    private val handle: Handle
) : GeneralRepository {

    /**
     * Gets information about the system
     */
    override fun getSystemInfo(): SystemInfo {
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

        val rankingDto = handle.createQuery("""select * from rankingview""")
            .mapTo<PlayerStatisticDTO>()
            .toList()

        val ranking = rankingDto.mapIndexed {  index, dto ->
            PlayerStatistics(index,dto.playerId,dto.totalGames,dto.wins)
        }

        return GameStatistics(numGames, ranking)
    }

}
