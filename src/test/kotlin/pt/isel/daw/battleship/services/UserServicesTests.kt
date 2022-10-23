package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.exception.UserAlreadyExistsException
import pt.isel.daw.battleship.services.validationEntities.UserValidation

class UserServicesTests {

    @Test
    fun `create a user successfully and authenticate after to confirm it`() {
        testWithTransactionManagerAndRollback {
            val userService = UserService(it)
            val (uid, token) = userService.createUser(UserValidation("user_test", "password"))
            val userID = userService.getUserIDFromToken(token)

            assertEquals(uid, userID)

            val authInfo = userService.authenticate(UserValidation("user_test", "password"))

            if (authInfo == null) {
                assert(false)
                return@testWithTransactionManagerAndRollback
            }

            assertEquals(uid, authInfo.uid)
            assertEquals(token, authInfo.token)
        }
    }

    @Test
    fun `create a user that already exists fails and throws UserAlreadyExistsException`() {
        assertThrows<UserAlreadyExistsException> {
            testWithTransactionManagerAndRollback {
                val userService = UserService(it)
                userService.createUser(UserValidation("user_test", "password"))
                userService.createUser(UserValidation("user_test", "password"))
            }
        }
    }
}