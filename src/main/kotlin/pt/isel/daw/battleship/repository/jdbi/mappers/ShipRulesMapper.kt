package pt.isel.daw.battleship.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import org.postgresql.util.PGobject
import pt.isel.daw.battleship.model.GameRules
import pt.isel.daw.battleship.repository.jdbi.JdbiGamesRepository

import java.sql.ResultSet

class ShipRulesMapper: ColumnMapper<GameRules.ShipRules> {
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): GameRules.ShipRules {
        val obj = rs.getObject(columnNumber, PGobject::class.java)
        return JdbiGamesRepository.deserializeShipRulesFromJson(obj.value ?: throw IllegalArgumentException("TODO"))
    }
}


