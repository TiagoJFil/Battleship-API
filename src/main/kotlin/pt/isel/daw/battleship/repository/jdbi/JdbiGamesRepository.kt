package pt.isel.daw.battleship.repository.jdbi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jdbi.v3.core.Handle

import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.result.ResultBearing
import org.jdbi.v3.core.statement.Update
import org.postgresql.util.PGobject
import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.model.GameRules.*
import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.services.dto.GameDTO

class JdbiGamesRepository(
    private val handle: Handle
) : GameRepository {
    override fun getGame(gameID: Id): Game? {
        return handle.createQuery("""
                select * from gamesview g where g.id = :id
             """)
            .bind("id", GameView.ID)
            .mapTo<GameDTO>()
            .firstOrNull()
            ?.toGame()
    }

    /**
     * Gets a game in waiting state.
     */
    override fun getWaitingStateGame(): Game? {
        return handle.createQuery("""Select * from gameview where state = 'waiting_player' order by id asc""")
            .mapTo<GameDTO>()
            .firstOrNull()
            ?.toGame()
    }

    /**
     * Creates a new game for the given user.
     * @param userID
     * @return [Game]
     */
    private fun insert(game: GameDTO): Id {
        val gameViewColumnNames = GameView.values().filter { it != GameView.SHIP_RULES }
        handle.createUpdate("""          
            Insert into gameview(
                ${gameViewColumnNames.joinToString(", ") { it.columnName }}, shiprules
            ) values (
             ${ gameViewColumnNames.joinToString(", ") { ":${it.columnName}" } }, cast(:shiprules as jsonb)
             )
            """
        ).bindGameDTO(game)
            .execute()

        return handle.createQuery("select max(id) from gameview")
            .mapTo<Id>()
            .first()
    }

      private fun update(game: GameDTO): Id?{
        val gameViewColumnNames = GameView.values().filter { it != GameView.SHIP_RULES }
        return handle.createUpdate("""
            update gameview set ${gameViewColumnNames.joinToString(", ") { "${it.columnName} = :${it.columnName}" }},
            shipRules = cast(:shiprules as jsonb)
            where id = :id
        """
        ).bindGameDTO(game)
            .bind("id", game.id)
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .firstOrNull()
    }

    private fun Update.bindMultiple(values: List<Pair<String, Any?>>): Update =
         values.fold(this){acc, pair ->
              acc.bind(pair.first, pair.second)

        }

    private fun Update.bindGameDTO(game: GameDTO): Update {
        return bindMultiple(
            listOf<Pair<String, Any?>>(
                GameView.ID.columnName to game.id,
                GameView.STATE.columnName to game.state,
                GameView.TURN.columnName to game.turn,
                GameView.PLAYER1.columnName to game.player1,
                GameView.PLAYER2.columnName to game.player2,
                GameView.BOARD_P1.columnName to game.boardP1,
                GameView.BOARD_P2.columnName to game.boardP2,
            )
        ).bindGameRules(game.rules)
    }

    private fun Update.bindGameRules(rules: GameRules): Update {
        return bindMultiple(
            listOf<Pair<String,Any?>>(
                GameView.SHOTS_PER_TURN.columnName to rules.shotsPerTurn,
                GameView.BOARD_SIDE.columnName to rules.boardSide,
                GameView.PLAY_TIMEOUT.columnName to rules.playTimeout,
                GameView.LAYOUT_DEFINITION_TIMEOUT.columnName to rules.layoutDefinitionTimeout,
                GameView.SHIP_RULES.columnName to serializeShipRulesToJson(rules.shipRules)
            )
        )



        bind("shotsperturn", rules.shotsPerTurn)
            .bind("boardside", rules.boardSide)
            .bind("playtimeout", rules.playTimeout)
            .bind("layoutdefinitiontimeout", rules.layoutDefinitionTimeout)
            .bind("shiprules", serializeShipRulesToJson(rules.shipRules))
    }

    override fun persist(game: GameDTO): Id?{
        if(!hasGame(game.id)){
            return insert(game)
        }
        return update(game)

    }

    private fun hasGame(gameID: Id?): Boolean{
        if(gameID == null) return false
        return handle.createQuery("""
            select exists(select 1 from game where id = :id)
        """).bind("id", gameID)
            .mapTo<Boolean>()
            .first()
    }

    companion object {
        private fun Update.bindShipRules(name: String, shipRules: ShipRules) = run {
            bind(
                name,
                PGobject().apply {
                    type = "jsonb"
                    value = serializeShipRulesToJson(shipRules)
                }
            )
        }

        private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        private fun serializeShipRulesToJson(shipRules: ShipRules): String = objectMapper.writeValueAsString(shipRules)

        fun deserializeShipRulesFromJson(json: String): ShipRules {
            return objectMapper.readValue(json, ShipRules::class.java)
        }
    }
}

enum class GameView(val columnName: String) {
    ID("id"),
    BOARD_SIDE("boardside"),
    SHOTS_PER_TURN("shotsperturn"),
    LAYOUT_DEFINITION_TIMEOUT("layoutdefinitiontimeout"),
    PLAY_TIMEOUT("playtimeout"),
    STATE("state"),
    TURN("turn"),
    PLAYER1("player1"),
    PLAYER2("player2"),
    BOARD_P1("boardp1"),
    BOARD_P2("boardp2"),
    SHIP_RULES("shiprules")
}

