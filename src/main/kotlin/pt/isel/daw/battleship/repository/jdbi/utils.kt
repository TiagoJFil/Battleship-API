package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.daw.battleship.repository.jdbi.mappers.ShipRulesMapper


/**
 * Installs the necessary plugins for the Jdbi instance to work in the application.
 */
fun Jdbi.configure(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())
    registerColumnMapper(ShipRulesMapper())
    return this
}