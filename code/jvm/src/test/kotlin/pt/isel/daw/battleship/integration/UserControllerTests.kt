package pt.isel.daw.battleship.integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.repository.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests {

    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun getTransactionFactory() = JdbiTransactionFactoryTestDB()
    }

    @BeforeEach
    fun executeClean() {
        executeWithHandle { handle ->
            clear()
        }
    }

    private val client by lazy {
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port/api")
            .build()
    }

    @Test
    fun `create a user returns 201`(){
        client.post().uri(Uris.User.REGISTER)
            .bodyValue("""{"username":"test1","password":"testad1"}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated
            .expectHeader()
            .assertContentTypeSiren()
            .expectBody()
            .jsonPath("\$.properties.uid").isNumber
    }

    @Test
    fun `invalid params on body returns 400`(){
        client.post().uri(Uris.User.REGISTER)
            .bodyValue("""{"username":"abc","password":""}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader()
            .assertContentTypeProblem()
    }


    @Test
    fun `creating a user with a repeated username returns 409`(){
        client.createUser("test1","testad1")

        client.post().uri(Uris.User.REGISTER)
            .bodyValue("""{"username":"test1","password":"testad1"}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isConflict
            .expectHeader()
            .assertContentTypeProblem()
    }

    @Test
    fun `log in sucessfully`(){
        val auth = client.createUser("test1","testad1")

        client.post().uri(Uris.User.LOGIN)
            .bodyValue("""{"username":"test1","password":"testad1"}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk
            .expectHeader()
            .assertContentTypeSiren()
            .expectBody()
            .jsonPath("\$.properties.uid").isEqualTo(auth.uid)
            .jsonPath("\$.properties.token").isEqualTo(auth.token)
    }

    @Test
    fun `try to log in with invalid credentials`(){
        client.createUser("test1","testad1")

        client.post().uri(Uris.User.LOGIN)
            .bodyValue("""{"username":"test1","password":"testad2"}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader()
            .assertContentTypeProblem()
    }

    @Test
    fun `try to log in with invalid username`(){
        client.createUser("test1","testad1")

        client.post().uri(Uris.User.LOGIN)
            .bodyValue("""{"username":"test2","password":"testad1"}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader()
            .assertContentTypeProblem()
    }

    @Test
    fun `missing parameter on register`(){
        client.post().uri(Uris.User.REGISTER)
            .bodyValue("""{"username":"test1"}""")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader()
            .assertContentTypeProblem()
    }



}