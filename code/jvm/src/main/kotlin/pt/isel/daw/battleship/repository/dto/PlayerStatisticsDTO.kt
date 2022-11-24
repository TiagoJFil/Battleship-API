package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.utils.ID

/**
 * Data transfer object for the PlayerStatistics
 */
data class PlayerStatisticDTO(val playerId : ID, val totalGames: Int, val wins: Int)
