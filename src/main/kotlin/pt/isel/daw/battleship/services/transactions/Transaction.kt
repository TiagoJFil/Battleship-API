package pt.isel.daw.battleship.services.transactions

import pt.isel.daw.battleship.repository.GameRepository

interface Transaction {

    val gamesRepository: GameRepository

    fun rollback()
}