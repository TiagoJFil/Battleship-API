package pt.isel.daw.battleship.services.transactions.jdbi

import org.jdbi.v3.core.Jdbi
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory

class JdbiTransactionFactory(
    private val jdbi: Jdbi
) : TransactionFactory {
    override fun <R> execute(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }


}
