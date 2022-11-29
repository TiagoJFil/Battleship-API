package pt.isel.daw.battleship.services.entities


data class Statistics(
    val nGames: Int,
    val ranking: List<PlayerStatistics>
)