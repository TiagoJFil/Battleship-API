package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.domain.GameRules

data class GameRulesDTO(
    val id: Int,
    val boardSide: Int,
    val shotsPerTurn: Int,
    val layoutDefinitionTimeout: Int,
    val playTimeout: Int,
    val shipRules: GameRules.ShipRules
)


fun GameRulesDTO.toGameRules() = GameRules(
    boardSide = boardSide,
    shotsPerTurn = shotsPerTurn,
    layoutDefinitionTimeout = layoutDefinitionTimeout,
    playTimeout = playTimeout,
    shipRules = shipRules
)