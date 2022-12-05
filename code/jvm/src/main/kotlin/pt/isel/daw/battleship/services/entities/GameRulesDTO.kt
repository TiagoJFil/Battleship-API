package pt.isel.daw.battleship.services.entities

import pt.isel.daw.battleship.domain.GameRules
import pt.isel.daw.battleship.utils.TimeoutTime

data class GameRulesDTO(
    val boardSide: Int,
    val shotsPerTurn: Int,
    val layoutDefinitionTimeout: TimeoutTime,
    val playTimeout: TimeoutTime,
    val shipRules: GameRules.ShipRules
)


fun GameRulesDTO.toGameRules() = GameRules(
    boardSide = boardSide,
    shotsPerTurn = shotsPerTurn,
    layoutDefinitionTimeout = layoutDefinitionTimeout,
    playTimeout = playTimeout,
    shipRules = shipRules
)

fun GameRules.toDTO() = GameRulesDTO(
    boardSide = boardSide,
    shotsPerTurn = shotsPerTurn,
    layoutDefinitionTimeout = layoutDefinitionTimeout,
    playTimeout = playTimeout,
    shipRules = shipRules
)