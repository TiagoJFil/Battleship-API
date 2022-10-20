package pt.isel.daw.battleship.repository

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransaction

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL("jdbc:postgresql://localhost:5432/world?user=postgres&password=docker")
        //setURL("jdbc:postgresql://localhost:49153/postgres?user=postgres&password=postgresw")
    }
).configure()

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

