package pt.isel.daw.battleship.controller.dto.input

import pt.isel.daw.battleship.domain.Square

/**
 * Represents the input model for the shots definition
 */
data class ShotsInfoInputModel(
    val shots: List<Square>
)