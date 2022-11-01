package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo

interface GeneralRepository {

    /**
     * Returns the system information.
     */
    fun getSystemInfo(): SystemInfo

    /**
     * Gets the game statistics
     */
    fun getStatistics(): GameStatistics


}