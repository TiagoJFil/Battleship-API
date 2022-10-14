package pt.isel.daw.battleship.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.daw.battleship.model.Game
import java.sql.ResultSet
import java.sql.SQLException

class GameMapper: ColumnMapper<Game> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet?, columnNumber: Int, ctx: StatementContext?): Game {
        TODO("Not yet implemented")
    }

}