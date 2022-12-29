package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import pt.isel.daw.battleship.controller.pipeline.exceptions.domainToAppExceptionMap
import pt.isel.daw.battleship.domain.DomainException
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
                if(e is DomainException){
                    val appExceptionConstructor = domainToAppExceptionMap[e::class]?.constructors?.first()
                    require(appExceptionConstructor != null) { "No AppException mapped to ${e::class}" }
                    throw appExceptionConstructor.call(e.message)
                }
                e.printStackTrace()

                throw InternalErrorAppException()
            }
        }
    }
}
