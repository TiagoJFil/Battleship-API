
package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.domain.board.*
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.TimeoutTime
import pt.isel.daw.battleship.utils.UserID

/**
 * Represents a state of a battleship game.
 */
data class Game(
    val id: ID?,
    val state: State,
    val rules: GameRules,
    val userToBoards: Map<UserID, Board>,
    val turnID: UserID,
    val lastUpdated: TimeoutTime = System.currentTimeMillis()
) {

    companion object;

    init {
        val playerBoards = userToBoards.values


        requireGameRule(playerBoards.all { it.side == rules.boardSide }) {
            "Board's side length is different from the rules"
        }

        require(userToBoards.size == 2){ "Game must have exactly 2 players" }

        // Check fleet composition
        if (state == State.PLAYING)
            check(playerBoards.all { it.fleetComposition == rules.shipRules.fleetComposition }) {
                "Fleet composition is different from the rules"
            }
    }

    val oppositeTurnID by afterGameBegins { userToBoards.keys.first { it != turnID } }

    val oppositeTurnBoard: Board by afterGameBegins {
        userToBoards[oppositeTurnID] ?: error("No board for the opposite turn ID")
    }

    val winnerId by
    lazy {
        if (state == State.FINISHED) {
            userToBoards.keys.single{ !userToBoards[it]!!.isInEndGameState() }
        } else null
    }


    /**
     * Returns a lazy property delegate that is only available after the game has begun.
     * @throws IllegalStateException if the game has not yet begun.
     */
    private fun <T> afterGameBegins(initializer: () -> T): Lazy<T> {
        return lazy {
            check(userToBoards.size == 2 && (state == State.PLAYING || state == State.FINISHED)) { "Can't access this property before the game begins." }
            initializer()
        }
    }

    /**
     * Represents the possible States of a game.
     */
    enum class State {
        PLACING_SHIPS,
        PLAYING,
        FINISHED,
        CANCELLED
    }
}

/**
 * Returns a new game after a shot is made on the specified [Square]
 *
 * @param squares the squares to shoot on
 * @throws IllegalArgumentException if a different number of shots is made than the rules allow
 * or if the game is not in the [Game.State.PLAYING] state
 */
fun Game.makePlay(squares: List<Square>): Game {
    requireGameState(Game.State.PLAYING)

    if (ranOutOfTimeFor(rules.playTimeout)) {
        return this.copy(state = Game.State.CANCELLED)
    }

    requireGameRule(squares.size == rules.shotsPerTurn){
        "A play requires exactly ${rules.shotsPerTurn} shots."
    }

    val newBoard = oppositeTurnBoard.makeShots(squares)
    val gameWithNewBoards = replaceBoard(oppositeTurnID, newBoard)

    return gameWithNewBoards
        .copy(
            turnID = oppositeTurnID,
            state =
            if (gameWithNewBoards.userToBoards.values.any { it.isInEndGameState() })
                Game.State.FINISHED
            else
                state,
            lastUpdated = System.currentTimeMillis()
        )
}

/**
 * Returns a new fresh game
 */
fun Game.Companion.new(players: Pair<UserID, UserID>, rules: GameRules) = Game(
    id = null,
    state = Game.State.PLACING_SHIPS,
    rules = rules,
    userToBoards = mapOf(players.first to Board.empty(rules.boardSide), players.second to Board.empty(rules.boardSide)),
    turnID = players.first,
    lastUpdated = System.currentTimeMillis()
)


private fun Game.ranOutOfTimeFor(timeout: Long) = System.currentTimeMillis() - lastUpdated > timeout

/**
 * Returns a new game after placing the ships on the board
 * @param shipList the list of ships to place
 * @param playerID the player that is placing the ships
 * @throws IllegalArgumentException if the ship is invalid according to the [Game.rules]
 */
fun Game.placeShips(shipList: List<ShipInfo>, playerID: UserID): Game {
    requireGameState(Game.State.PLACING_SHIPS)

    if (ranOutOfTimeFor(rules.layoutDefinitionTimeout)) {
        return this.copy(state = Game.State.CANCELLED)
    }

    val emptyBoard = Board.empty(this.rules.boardSide)
    val newBoard = emptyBoard.placeShips(shipList)

    requireGameRule(newBoard.fleetComposition == rules.shipRules.fleetComposition){
        "Require the following fleet composition: " +
        rules.shipRules.fleetComposition.entries.joinToString(", "){
            "${it.value} boats of ${it.key} squares."
        }
    }

    val newGameState = this.replaceBoard(playerID, newBoard)
    val hasBothBoardsNotEmpty = newGameState.userToBoards.values.all { it != emptyBoard }

    return if (hasBothBoardsNotEmpty)
        newGameState.copy(state = Game.State.PLAYING, lastUpdated = System.currentTimeMillis())
    else
        newGameState

}

/**
 * Returns a new Game after the board from [id] is replaced by [newBoard]
 * @param id the player whose board is to be replaced
 * @param newBoard the new board
 * @return [Game] a new Game with the new board
 */
private fun Game.replaceBoard(id: UserID, newBoard: Board) = copy(
    userToBoards = this.userToBoards.mapValues { entry ->
        if (entry.key == id)
            newBoard
        else
            entry.value
    }
)





