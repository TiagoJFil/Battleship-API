package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.exception.InvalidParameterException
import pt.isel.daw.battleship.services.exception.UnauthenticatedAppException
import pt.isel.daw.battleship.services.exception.UserAlreadyExistsException
import pt.isel.daw.battleship.services.validationEntities.UserValidation

class UserServicesTests {

    @Test
    fun `create a user successfully and authenticate after to confirm it`() {
        testWithTransactionManagerAndRollback {
            val userService = UserService(this)

            val (uid, token) = userService.createUser(UserValidation("user_test", "password1"))
            val userID = userService.getUserIDFromToken(token)

            assertEquals(uid, userID)

            val authInfo = userService.authenticate(UserValidation("user_test", "password1"))



            assertEquals(uid, authInfo.uid)
            assertEquals(token, authInfo.token)
        }
    }

    @Test
    fun `cant create a user that has a weak password`(){
        testWithTransactionManagerAndRollback {
            val userService = UserService(this)
            assertThrows<InvalidParameterException> {
                userService.createUser(UserValidation("user_test", "123"))
            }
        }
    }

    @Test
    fun `cant create a user with a very large username`(){
        testWithTransactionManagerAndRollback {
            val userService = UserService(this)
            assertThrows<InvalidParameterException> {
                userService.createUser(UserValidation("abcdefghabcdeghabcdeghabcdeghabcdeghabcdegh","password1"))
            }
        }
    }

    @Test
    fun `create a user that already exists fails and throws UserAlreadyExistsException`() {
        assertThrows<UserAlreadyExistsException> {
            testWithTransactionManagerAndRollback {
                val userService = UserService(this)
                userService.createUser(UserValidation("user_test", "password1"))
                userService.createUser(UserValidation("user_test", "password1"))
            }
        }
    }

    @Test
    fun `create a user with less than 3 characters throws InvalidParameterException`(){
        testWithTransactionManagerAndRollback {
            val userService = UserService(this)
            assertThrows<InvalidParameterException> {
                userService.createUser(UserValidation("ab","password1"))
            }
        }
    }

    @Test
    fun `get the user id from an unexistent token throws UnauthenticatedAppException`(){
        testWithTransactionManagerAndRollback {
            val userService = UserService(this)
            assertThrows<UnauthenticatedAppException> {
                userService.getUserIDFromToken(null)
            }
        }
    }
}