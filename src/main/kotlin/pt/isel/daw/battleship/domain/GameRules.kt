package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.utils.ShipCount
import pt.isel.daw.battleship.utils.ShipSize
import pt.isel.daw.battleship.utils.TimeoutTime
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class GameRules(
    val shotsPerTurn: Int,
    val boardSide: Int,
    val playTimeout: TimeoutTime,
    val layoutDefinitionTimeout: TimeoutTime,
    val shipRules: ShipRules
) {

    data class ShipRules(
        val name: String, val fleetComposition: Map<ShipSize, ShipCount>
    )

    companion object {

        val DEFAULT = GameRules(
            1,
            10,
            Duration.ofMinutes(1).toMillis(),
            Duration.ofMinutes(1).toMillis(),
            ShipRules(
                "Classic",
                mapOf<ShipSize, ShipCount>(
                    5 to 1,
                    4 to 1,
                    3 to 1,
                    2 to 1,
                    1 to 1
                )
            )
        )
    }
}



