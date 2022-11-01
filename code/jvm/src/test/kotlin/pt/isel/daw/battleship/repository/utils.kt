package pt.isel.daw.battleship.repository

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertTrue
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.HeaderAssertions
import org.springframework.test.web.reactive.server.StatusAssertions
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
        val url =System.getenv("JDBC_TEST_DATABASE_URL") ?: throw IllegalStateException("JDBC_TEST_DATABASE_URL not set")
        setURL(url)
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
            }
        }
    }
}

fun clear(){
    executeWithHandle { handle ->
        handle.execute("""
            delete from board cascade;
            delete from game cascade;
            delete from gamerules cascade;
            delete from shiprules cascade;
            delete from token  cascade;
            delete from waitinglobby cascade;
            delete from "User" cascade;
        """.trimIndent())

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

