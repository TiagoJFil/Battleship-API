package pt.isel.daw.battleship.api

import org.junit.jupiter.api.Assertions
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.HeaderAssertions
import org.springframework.test.web.reactive.server.StatusAssertions
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.ProblemContentType
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenContentType
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.GameInformation
import pt.isel.daw.battleship.utils.ID


fun WebTestClient.createUser(username: String, password: String): AuthInformation {
    val body = this.post().uri(Uris.User.REGISTER)
        .bodyValue("""{"username":"$username","password":"$password"}""")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isCreated
        .expectHeader()
        .assertContentTypeSiren()
        .expectBody<SirenEntity<AuthInformation>>()
        .returnResult()
        .responseBody!!
    val id = body.properties?.uid
    val token = body.properties?.token

    return AuthInformation(id!!, token!!)
}

fun WebTestClient.joinQueue(authToken: String): ID? {
    val res = this.post().uri(Uris.Lobby.QUEUE)
        .setAuthToken(authToken)
        .exchange()
        .expectStatus().isOk
        .expectHeader()
        .assertContentTypeSiren()
        .expectBody<SirenEntity<GameInformation>>()
        .returnResult()
        .responseBody!!

    return res.properties?.id
}



/**
 * Asserts that the response has the Siren content type.
 */
fun HeaderAssertions.assertContentTypeSiren()  =
    this.value("Content-Type") {
        Assertions.assertTrue(it.equals(SirenContentType))
    }

/**
 * Asserts that the response has the Problem content type.
 */
fun HeaderAssertions.assertContentTypeProblem()  =
    this.value("Content-Type") {
        Assertions.assertTrue(it.equals(ProblemContentType))
    }


val StatusAssertions.isConflict
    get() = isEqualTo(HttpStatus.CONFLICT)


fun WebTestClient.RequestHeadersSpec<*>.setAuthToken(token: String) = header("Authorization", "Bearer $token")