package pt.isel.daw.battleship.services.transactions

interface TransactionFactory {
    fun <R> execute(block: Transaction.() -> R): R
}