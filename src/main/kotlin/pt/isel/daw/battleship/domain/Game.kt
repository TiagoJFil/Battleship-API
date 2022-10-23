package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.utils.UserID

/**
 * Represents a state of a battleship game.
 */
data class Game(
    val id: Id?,
    val state: State,
    val rules: GameRules = GameRules.DEFAULT,
    val boards: Map<UserID, Board>,
    val turnID: UserID
) {

    companion object;

    init {
        val playerBoards = boards.values


        require(playerBoards.all { it.side == rules.boardSide }) { "Board's side length is different from the rules" }
        require(boards.size == 2)

        // Check fleet composition
        if (state == State.PLAYING)
            check(playerBoards.all { it.fleetComposition == rules.shipRules.fleetComposition })
    }

    val turnBoard by afterGameBegins { boards.keys.first { it != turnID } }

    val oppositeTurnID by afterGameBegins { boards.keys.first { it != turnID } }

    val oppositeTurnBoard: Board by afterGameBegins { boards[oppositeTurnID] ?: error("No board for the opposite turn ID") }

    /**
     * Returns a lazy property delegate that is only available after the game has begun.
     * @throws IllegalStateException if the game has not yet begun.
     */
    private fun <T> afterGameBegins(initializer: () -> T): Lazy<T> {
        return lazy{
            check(boards.size == 2 && (state == State.PLAYING || state == State.FINISHED)) { "Can't access this property before the game begins." }
            initializer()
        }
    }

    enum class State {
        PLACING_SHIPS,
        PLAYING,
        FINISHED
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

    require(state == Game.State.PLAYING) { "Game is not in a playable state." }
    require(squares.size == rules.shotsPerTurn) {
        "Invalid number of shots. Only ${rules.shotsPerTurn} shots allowed per play."
    }

    val newBoard = oppositeTurnBoard.makeShots(squares)
    val gameWithNewBoards = this.replaceBoard(oppositeTurnID, newBoard)


    return gameWithNewBoards
        .copy(
            turnID = oppositeTurnID,
            state =
            if (gameWithNewBoards.boards.values.any { it.isInEndGameState() })
                Game.State.FINISHED
            else
                state
        )
}

/**
 * Returns a new fresh game
 */
fun Game.Companion.new(players: Pair<UserID, UserID>,  rules: GameRules) = Game(
    id = null,
    state = Game.State.PLACING_SHIPS,
    rules = rules,
    boards = mapOf(players.first to Board.empty(rules.boardSide), players.second to Board.empty(rules.boardSide)),
    turnID = players.first
)


/**
 * Returns true if the game is over
 */
fun Game.isOver() = state == Game.State.FINISHED


/**
 * Returns a new game after placing the ships on the board
 * @param shipList the list of ships to place
 * @param playerID the player that is placing the ships
 * @throws IllegalArgumentException if the ship is invalid according to the [Game.rules]
 */
fun Game.placeShips(shipList: List<ShipInfo>, playerID: UserID): Game {
    require(state == Game.State.PLACING_SHIPS) { "It is not the ship placing phase" }
    val emptyBoard = Board.empty(this.rules.boardSide)
    val newBoard = emptyBoard.placeShips(shipList)

    check(newBoard.fleetComposition == rules.shipRules.fleetComposition) {
        "Invalid ship composition. Expected ${rules.shipRules.fleetComposition}, got ${newBoard.fleetComposition}"
    }

    val newGameState = this.replaceBoard(playerID, newBoard)
    val hasBothBoardsNotEmpty = newGameState.boards.values.all { it != emptyBoard }

    return if(hasBothBoardsNotEmpty)
        newGameState.copy(state = Game.State.PLAYING)
    else
        newGameState

}

/**
 * Returns a new Game after the board from [turn] is replaced by [newBoard]
 * @param turn the player whose board is to be replaced
 * @param newBoard the new board
 * @return [Game] a new Game with the new board
 */
private fun Game.replaceBoard(turn: UserID, newBoard: Board) = copy(
    boards = this.boards.mapValues { entry ->
        if (entry.key == turn)
            newBoard
        else
            entry.value
    }
)





