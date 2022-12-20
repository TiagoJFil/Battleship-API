package pt.isel.daw.battleship.repository.jdbi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.statement.Update
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.GameRules.*
import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.repository.dto.*
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID


class JdbiGamesRepository(
    private val handle: Handle
) : GameRepository {

    /**
     * Gets the game with the given id
     * @param gameID the id of the game
     * @return [Game] the game
     */
    override fun get(gameID: ID): Game? {
        return handle.createQuery(
            """
                select * from gameview g where g.id = :id
             """
        )
            .bind(GameView.ID.columnName, gameID)
            .mapTo<GameDTO>()
            .firstOrNull()
            ?.toGame()
    }

    /**
     * Persists the given game in the database
     * @param game the game to be persisted
     * @return [ID] of the game persisted
     */
    override fun persist(game: GameDTO): ID {
        if (!hasGame(game.id)) {
            return insert(game)
        }
        require(game.id != null)
        update(game)
        return game.id
    }

    /**
     * Gets all the games that the given user is participating in and that are not finished
     * @param userID the [UserID] of the user
     */
    override fun getUserGames(userID: UserID): List<ID> {
        return handle.createQuery("select id from game where (state = 'playing' or state = 'placing_ships') and (player1 = :userID or player2 = :userID)")
            .bind("userID", userID)
            .mapTo<ID>()
            .list()
    }

    /**
     * Inserts a new game in the database
     * @param game the game data transfer object to be persisted
     * @return [ID] of the game created
     */
    private fun insert(game: GameDTO): ID {
        val gameViewColumnNames = GameView.values().filter { it != GameView.SHIP_RULES && it != GameView.LAST_UPDATED }
        handle.createUpdate("""          
            Insert into gameview(
                ${gameViewColumnNames.joinToString(", ") { it.columnName }}, shiprules, lastupdated
            ) values (
             ${gameViewColumnNames.joinToString(", ") { ":${it.columnName}" }}, cast(:shiprules as jsonb),
             cast(:lastupdated as timestamp)
             )
            """
        ).bindGameDTO(game)
            .execute()

        return handle.createQuery("select max(id) from gameview")
            .mapTo<ID>()
            .first()
    }

    /**
     * Updates the given game in the database
     * @param game the game data transfer object to be persisted
     * @return [Id] of the game updated
     */
    private fun update(game: GameDTO) {
        val gameViewColumnNames = GameView.values().filter { it != GameView.SHIP_RULES && it != GameView.LAST_UPDATED }

        handle.createUpdate(
            """
            update gameview set ${gameViewColumnNames.joinToString(", ") { "${it.columnName} = :${it.columnName}" }},
            shipRules = cast(:shiprules as jsonb), lastUpdated = cast(:lastupdated as timestamp)
            where id = :id
        """
        ).bindGameDTO(game)
            .bind("id", game.id)
            .execute()
    }

    /**
     * Binds multiple parameters to the given [Update] object
     * @param values the name and value of the parameters to be bound
     * @return the [Update] object with the parameters bound
     */
    private fun Update.bindMultiple(values: List<Pair<String, Any?>>): Update =
        values.fold(this) { acc, pair ->
            acc.bind(pair.first, pair.second)
        }

    /**
     * Binds the game data transfer object to the given [Update] object
     * @param game the game data transfer object to be bound
     * @return the [Update] object with the parameters bound
     */
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
                GameView.LAST_UPDATED.columnName to game.lastUpdated
            )
        ).bindGameRules(game.rules)
    }

    /**
     * Binds the game rules to the given [Update] object
     * @param rules the game rules to be bound
     * @return the [Update] object with the parameters bound
     */
    private fun Update.bindGameRules(rules: GameRules): Update {
        return bindMultiple(
            listOf<Pair<String, Any?>>(
                GameView.SHOTS_PER_TURN.columnName to rules.shotsPerTurn,
                GameView.BOARD_SIDE.columnName to rules.boardSide,
                GameView.PLAY_TIMEOUT.columnName to rules.playTimeout,
                GameView.LAYOUT_DEFINITION_TIMEOUT.columnName to rules.layoutDefinitionTimeout,
                GameView.SHIP_RULES.columnName to serializeShipRulesToJson(rules.shipRules)
            )
        )
    }


    /**
     * Checks if the game with the given id exists in the database
     * @param gameID the id of the game
     * @return [Boolean] true if the game exists, false otherwise
     */
    private fun hasGame(gameID: ID?): Boolean {
        if (gameID == null) return false
        return handle.createQuery(
            """
            select exists(select 1 from game where id = :id)
        """
        ).bind("id", gameID)
            .mapTo<Boolean>()
            .first()
    }

    companion object {
        private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        /**
         * Serializes the given ship rules to a json string
         * @param shipRules the ship rules to be serialized
         * @return [String] the json string
         */
        fun serializeShipRulesToJson(shipRules: ShipRules): String = objectMapper.writeValueAsString(shipRules)

        /**
         * Deserializes the given json string to a ship rules object
         * @param json the json string to be deserialized
         * @return [ShipRules] the ship rules object
         */
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
    SHIP_RULES("shiprules"),
    LAST_UPDATED("lastupdated")
}

