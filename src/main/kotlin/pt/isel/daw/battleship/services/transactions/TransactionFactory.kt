package pt.isel.daw.battleship.services.transactions

import org.springframework.stereotype.Component

@Component
interface TransactionFactory {
    fun <R> execute(block: Transaction.() -> R): R
}