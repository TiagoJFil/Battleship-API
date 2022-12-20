package pt.isel.daw.battleship.services

import org.springframework.stereotype.Service
import pt.isel.daw.battleship.services.entities.EmbeddableStatistics
import pt.isel.daw.battleship.services.entities.Statistics
import pt.isel.daw.battleship.services.entities.SystemInfo
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.services.transactions.TransactionFactory

@Service
class GeneralService (
    private val transactionFactory: TransactionFactory
) {
    /**
     * Gets information about the system
     */
    fun getSystemInfo(): SystemInfo {
        return transactionFactory.execute {
            generalRepository.getSystemInfo()
        }
    }

    /**
     * Gets the game statistics
     */
    fun getStatistics(embedded: Boolean = false): EmbeddableStatistics {
        return transactionFactory.execute {
            val statistics = generalRepository.getStatistics()
            val users = statistics.ranking.mapNotNull { userRepository.getUser(it.playerID) }
            EmbeddableStatistics(statistics,users)
        }
    }

}