package pt.isel.daw.battleship.services.transactions.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.exception.AppException
import pt.isel.daw.battleship.services.exception.InternalErrorAppException
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory

@Component
class JdbiTransactionFactory(
    private val jdbi: Jdbi
) : TransactionFactory {

    override fun <R> execute(block: Transaction.() -> R): R {
        return jdbi.inTransaction<R, Exception> { handle ->
            try {
                val transaction = JdbiTransaction(handle)
                block(transaction)
            } catch (e: Exception) {
                if (e is AppException) throw e
                e.printStackTrace()

                throw InternalErrorAppException()
            }

            }

    }
}
