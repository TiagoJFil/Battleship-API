package pt.isel.daw.battleship.services.entities

import pt.isel.daw.battleship.utils.UserName

data class GameStatistics(
    val nGames: Int,
    val ranking: List<Pair<UserName, Int>>
)