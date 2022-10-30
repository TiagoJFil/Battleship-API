package pt.isel.daw.battleship.repository

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.test.web.reactive.server.HeaderAssertions
import pt.isel.daw.battleship.controller.hypermedia.ProblemContentType
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenContentType
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.services.exception.AppException
import pt.isel.daw.battleship.services.exception.InternalErrorAppException
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransaction
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransactionFactory

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=craquesdabola123")
        //setURL("jdbc:postgresql://localhost:5432/world?user=postgres&password=docker")
        //setURL("jdbc:postgresql://localhost:49153/postgres?user=postgres&password=postgresw")
    }
).configure()

fun JdbiTransactionFactoryTestDB() = object : JdbiTransactionFactory(jdbi) {
    override fun <R> execute(block: Transaction.() -> R): R {
        return jdbi.inTransaction<R,Exception> {handle ->
            try{
                val transaction = JdbiTransaction(handle)
                 block(transaction)
            }catch(e : Exception){
                if (e is AppException) throw e
                e.printStackTrace()

                throw InternalErrorAppException()
            }finally {
                handle.rollback()
            }

        }
    }

}

fun executeWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception> { handle ->
    block(handle)
}

fun testWithTransactionManagerAndRollback(block: (TransactionFactory) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->

    val transaction = JdbiTransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionFactory {
        override fun <R> execute(block: (Transaction) -> R): R {
            return block(transaction)
        }

    }
    block(transactionManager)

    // finally, we rollback everything
    handle.rollback()
}


/**
 * Asserts that the response has the Siren content type.
 */
fun HeaderAssertions.assertContentTypeSiren()  =
    this.value("Content-Type") {
        assertTrue(it.equals(SirenContentType))
    }

/**
 * Asserts that the response has the Problem content type.
 */
fun HeaderAssertions.assertContentTypeProblem()  =
    this.value("Content-Type") {
        assertTrue(it.equals(ProblemContentType))
    }



