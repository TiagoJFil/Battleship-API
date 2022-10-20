package pt.isel.daw.battleship.services.transactions.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory

@Component
class JdbiTransactionFactory(
    private val jdbi: Jdbi
) : TransactionFactory {

    override fun <R> execute(block: Transaction.() -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }

}
