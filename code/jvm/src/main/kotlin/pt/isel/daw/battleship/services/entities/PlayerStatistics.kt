package pt.isel.daw.battleship.services.entities

import pt.isel.daw.battleship.utils.ID

data class PlayerStatistics(val rank : Int, val playerId : ID, val totalGames: Int, val wins: Int)