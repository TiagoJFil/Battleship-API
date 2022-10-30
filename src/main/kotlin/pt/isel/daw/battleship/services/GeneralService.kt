package pt.isel.daw.battleship.services

import org.springframework.stereotype.Service
import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.PlayerStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo
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
    fun getStatistics(): GameStatistics {
        return transactionFactory.execute {
            generalRepository.getStatistics()
        }
    }



}