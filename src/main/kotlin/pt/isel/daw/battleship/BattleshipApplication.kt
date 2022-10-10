package pt.isel.daw.battleship

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class BattleshipApplication{
	@Bean
	fun jdbi() = Jdbi.create("jdbc:postgresql://localhost/postgres?user=postgres&password=craquesdabola123")
		.installPlugin(KotlinPlugin())
}

fun main(args: Array<String>) {
	runApplication<BattleshipApplication>(*args)
}
