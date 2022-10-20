package pt.isel.daw.battleship.controller

import pt.isel.daw.battleship.model.Id
import pt.isel.daw.battleship.utils.UserID

data class PlayIntentModelInput(
    val gameId: Id,
    val userID: UserID
)


