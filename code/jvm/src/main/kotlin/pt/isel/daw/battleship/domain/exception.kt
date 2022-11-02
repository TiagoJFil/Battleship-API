package pt.isel.daw.battleship.domain

/**
 * Base class for all exception thrown by the domain layer.
 */
sealed class DomainException(message: String) : Exception(message)
class IllegalGameStateException(message: String): DomainException(message)
class GameRuleViolationException(message: String): DomainException(message)


/**
 * Function that ensures that the game is in the expected state.
 */
fun Game.requireGameState(expected: Game.State){
    if (state != expected)
        throw IllegalGameStateException("Game is in state $state. Should be in $expected")
}

/**
 * Throws a [GameRuleViolationException] if the condition is not met.
 */
fun requireGameRule(condition: Boolean, message: () -> String){
    if (!condition)
        throw GameRuleViolationException("Rule not met: "+ message())
}