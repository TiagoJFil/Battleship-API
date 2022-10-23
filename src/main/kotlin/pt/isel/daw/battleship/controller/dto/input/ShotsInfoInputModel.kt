package pt.isel.daw.battleship.controller.dto.input

import pt.isel.daw.battleship.model.Square

data class ShotsInfoInputModel(
    val shots: List<Square>
)