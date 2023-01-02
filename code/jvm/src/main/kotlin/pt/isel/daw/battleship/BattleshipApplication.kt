package pt.isel.daw.battleship

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import pt.isel.daw.battleship.repository.jdbi.configure


@SpringBootApplication
@EnableScheduling
class BattleshipApplication {

    @Value("\${spring.datasource.url}")
    private val dbUrl: String? = null

    @Bean
    fun jdbi() = Jdbi.create(dbUrl)
        .configure()

}

fun main(args: Array<String>) {
    runApplication<BattleshipApplication>(*args)
}
