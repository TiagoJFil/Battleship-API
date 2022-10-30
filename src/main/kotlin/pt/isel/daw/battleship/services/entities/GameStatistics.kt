package pt.isel.daw.battleship.services.entities


data class GameStatistics(
    val nGames: Int,
    val ranking: List<PlayerStatistics>
)