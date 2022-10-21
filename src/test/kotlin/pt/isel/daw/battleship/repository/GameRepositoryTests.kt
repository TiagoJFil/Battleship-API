package pt.isel.daw.battleship.repository


import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.controller.Uris

import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.model.Game.State.*
import pt.isel.daw.battleship.repository.dto.GameDTO
import pt.isel.daw.battleship.repository.dto.UserDTO
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.repository.jdbi.GameView
import pt.isel.daw.battleship.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.battleship.repository.jdbi.JdbiGamesRepository.Companion.serializeShipRulesToJson
import pt.isel.daw.battleship.utils.UserID

/*
class GameRepositoryTests {
    private val users = getNUsers(3)

    private val player1ID = users[0].id
    private val testGameID = 1
    private val gameDTO = GameDTO(
        id = null,
        state = PLACING_SHIPS.toString().lowercase(),
        rules = GameRules.DEFAULT,
        turn = player1ID,
        player1 = player1ID,
        player2 = null,
        boardP1 = null,
        boardP2 = null,
    )

    @BeforeEach
    fun setup() {
        executeWithHandle { handle ->
            clear()
            handle.insertUsers(users)
            handle.insertGame(gameDTO)
        }
    }


    @Test
    fun `test getGame with valid id`() {
        executeWithHandle { handle ->
            val gameRepo = JdbiGamesRepository(handle)
            val game = gameRepo.getGame(testGameID)
            assert(game != null)
            if(game != null) assertEquals(game.id, testGameID)
        }
    }

    @Test
    fun `test getGame with invalid id`() {
        executeWithHandle { handle ->
            val gameRepo = JdbiGamesRepository(handle)
            val game = gameRepo.getGame(0)
            assert(game == null)
        }
    }

    @Test
    fun `check if the rules are correctly inserted after a new game insertion`() {
        executeWithHandle { handle ->
            val gameRepo = JdbiGamesRepository(handle)
            val game = gameRepo.getGame(testGameID)
            assert(game != null)
            if(game != null){
                assertEquals(testGameID, game.id)
                assertEquals(GameRules.DEFAULT.boardSide,game.rules.boardSide)
                assertEquals(GameRules.DEFAULT.shipRules, game.rules.shipRules)
                assertEquals(GameRules.DEFAULT.shotsPerTurn, game.rules.shotsPerTurn)
                assertEquals(GameRules.DEFAULT.playTimeout, game.rules.playTimeout)
                assertEquals(GameRules.DEFAULT.layoutDefinitionTimeout, game.rules.layoutDefinitionTimeout)
            }
        }
    }

    private fun Handle.insertUsers(users: List<UserDTO>) {
       createUpdate("""
             insert into "User"(id, "name") values${users.joinToString(", ") { "(${it.id}, '${it.name}')" }}
        """).execute()
    }

    private fun Handle.insertGame(game: GameDTO) {
        val gameViewColumnNames = GameView.values().filter { it != GameView.SHIP_RULES && it != GameView.ID }
        val rules = game.rules
        createUpdate("""          
                insert into gameview(
                    ${gameViewColumnNames.joinToString(", ") { it.columnName }}, shiprules
                ) values (
                ${rules.boardSide}, ${rules.shotsPerTurn}, ${rules.layoutDefinitionTimeout}, ${rules.playTimeout}, 
                 '${game.state}', ${game.turn}, ${game.player1}, ${game.player2}, ${game.boardP1}, ${game.boardP2}, 
                 cast('${serializeShipRulesToJson(rules.shipRules)}' as jsonb)
                 )
            """
        ).execute()
    }

    private fun getNUsers(n: Int): List<UserDTO> {
        return (1..n).map { UserDTO(it, "user$it") }
    }

    private fun clear(){
        executeWithHandle { handle ->
            handle.execute("delete from board")
            handle.execute("delete from game")
            handle.execute("delete from gamerules")
            handle.execute("delete from shiprules")
            handle.execute("delete from \"User\"")
        }
    }
    companion object{
        @BeforeAll
        fun createTables(){
            executeWithHandle { handle->
                handle.execute("""
                begin;

                create table if not exists Authors(
                    name varchar(20) primary key,
                    email varchar(255) constraint emailinvalid check(email ~* '^[A-Z0-9.%-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}${'$'}'),
                    github varchar(255)
                );

                create table if not exists SystemInfo(
                    name varchar(20) primary key,
                    version varchar(20)
                );


                create table if not exists "User" (
                    id serial primary key,
                    "name" varchar(20) unique not null,
                );

                create table if not exists token(
                    token varchar(255) primary key,
                    userID int,
                    foreign key(userID) references "User"(id)
                );

                create table if not exists ShipRules(
                    id serial primary key,
                    fleetInfo jsonb
                );

                create table if not exists GameRules (
                    id serial primary key,
                    boardSide int,
                    shotsPerTurn int,
                    layoutDefinitionTimeout int,
                    playTimeout int,
                    shiprules int,
                    foreign key(shiprules) references ShipRules(id)
                );

                create table if not exists Game (
                    id serial primary key,
                    rules int, foreign key(rules) references GameRules(id),
                    "state" varchar(20) check ( "state" like 'placing_ships' or "state" like 'playing' or "state" like 'finished' or "state" like 'waiting_player'),
                    turn int,
                    player1 int, foreign key(player1) references "User"(id),
                    player2 int, foreign key(player2) references "User"(id),
                    winner int, foreign key(winner) references "User"(id),
                    foreign key(turn) references "User"(id)
                );

                create table if not exists Board (
                    layout text,
                    gameId int,
                    userId int,
                    primary key (gameId, userId),
                    foreign key (gameId) references Game(id),
                    foreign key (userId) references "User"(id)
                );
                commit;
            """
                )
            }
        }
    }

}

*/