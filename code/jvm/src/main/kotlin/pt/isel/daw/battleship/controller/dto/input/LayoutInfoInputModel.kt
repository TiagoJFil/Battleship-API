package pt.isel.daw.battleship.controller.dto.input

import pt.isel.daw.battleship.domain.board.ShipInfo

/**
 * Represents the input model for the layout definition
 */
data class LayoutInfoInputModel (
    val shipsInfo: List<ShipInfo>
)
