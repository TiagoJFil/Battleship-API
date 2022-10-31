package pt.isel.daw.battleship.services.entities

import pt.isel.daw.battleship.domain.Game.*
import pt.isel.daw.battleship.utils.UserID

data class GameStateInfo(
    val state: State,
    val winnerId: UserID?
)