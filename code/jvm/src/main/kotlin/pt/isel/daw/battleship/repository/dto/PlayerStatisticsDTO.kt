package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.utils.ID


data class PlayerStatisticDTO(val playerId : ID, val totalGames: Int, val wins: Int)
