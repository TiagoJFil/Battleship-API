package pt.isel.daw.battleship.services.entities


data class Statistics(
    val nGames: Int,
    val ranking: List<PlayerStatistics>
)


data class EmbeddableStatistics(
    val statistics: Statistics,
    val users: List<User>
)