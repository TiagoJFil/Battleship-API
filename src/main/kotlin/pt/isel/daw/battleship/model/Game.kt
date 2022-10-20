package pt.isel.daw.battleship.model

import pt.isel.daw.battleship.utils.UserID

/**
 * Represents a state of a battleship game.
 */
data class Game(
    val id: Id?,
    val state: State = State.WAITING_PLAYER,
    val rules: GameRules = GameRules.DEFAULT,
    val boards: Map<UserID, Board?>,
    val turnID: UserID
) {

    companion object;

    init {
        val playerBoards = boards.values

        if (state != State.WAITING_PLAYER) {
            require(playerBoards.all { it?.side == rules.boardSide }) { "Board's side length is different from the rules" }
            require(boards.size == 2)
        }
        // Check fleet composition
        if (state == State.PLAYING)
            check(playerBoards.all { it?.fleetComposition == rules.shipRules.fleetComposition })
    }

    val turnBoard by afterGameBegins { boards.keys.first { it != turnID } }

    val oppositeTurnID by afterGameBegins { boards.keys.first { it != turnID } }

    val oppositeTurnBoard by afterGameBegins { boards[oppositeTurnID] }

    /**
     * Returns a lazy property delegate that is only available after the game has begun.
     * @throws IllegalStateException if the game has not yet begun.
     */
    private fun <T> afterGameBegins(initializer: () -> T): Lazy<T> {
        check(boards.size == 2 && (state == State.PLAYING || state == State.FINISHED)) { "Can't access this property before the game begins." }
        return lazy(initializer)
    }

    enum class State {
        WAITING_PLAYER,
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

    val newBoard = oppositeTurnBoard?.makeShots(squares)
    val gameWithNewBoards = this.replaceBoard(oppositeTurnID, newBoard)


    return gameWithNewBoards
        .copy(
            turnID = oppositeTurnID,
            state =
            if (gameWithNewBoards.boards.values.any { it?.isInEndGameState() == true })
                Game.State.FINISHED
            else
                state
        )
}

/**
 * Returns a new fresh game
 */
fun Game.Companion.new(userID: UserID, rules: GameRules) = Game(
    id = null,
    state = Game.State.WAITING_PLAYER,
    rules = rules,
    boards = emptyMap(),
    turnID = userID
)


/**
 * Returns true if the game is over
 */
fun Game.isOver() = state == Game.State.FINISHED


/**
 * Returns a new game after placing the ships on the board
 *
 * @throws IllegalArgumentException if the ship is invalid according to the [Game.rules]
 */
fun Game.placeShips(shipList: List<ShipInfo>, playerID: UserID): Game {
    require(state == Game.State.PLACING_SHIPS) { "It is not the ship placing phase" }

    val newBoard = Board.empty(this.rules.boardSide).placeShips(shipList)

    check(newBoard.fleetComposition == rules.shipRules.fleetComposition) {
        "Invalid ship composition. Expected ${rules.shipRules.fleetComposition}, got ${newBoard.fleetComposition}"
    }

    return this.replaceBoard(playerID, newBoard)

}

/**
 * Returns a new Game after the board from [turn] is replaced by [newBoard]
 */
private fun Game.replaceBoard(turn: UserID, newBoard: Board?) = copy(
    boards = this.boards.mapValues { entry ->
        if (entry.key == turn)
            newBoard
        else
            entry.value
    }
)




