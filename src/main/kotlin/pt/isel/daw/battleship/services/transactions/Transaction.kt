package pt.isel.daw.battleship.services.transactions

import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.repository.UserRepository

interface Transaction {

    val gamesRepository: GameRepository
    val userRepository: UserRepository

    fun rollback()
}