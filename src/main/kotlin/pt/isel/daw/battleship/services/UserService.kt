package pt.isel.daw.battleship.services

import org.springframework.stereotype.Service
import pt.isel.daw.battleship.services.entities.UserInfo
import pt.isel.daw.battleship.services.exception.NotFoundAppException
import pt.isel.daw.battleship.services.exception.UserAlreadyExistsException
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.validationEntities.UserCreation
import pt.isel.daw.battleship.utils.UserID
import java.util.*

@Service
class UserService(
    val transactionFactory: TransactionFactory
) {

    /**
     * Creates a new user.
     * @param userCreation The user creation information.
     * @return [Result] with the user's ID.
     * @throws UserAlreadyExistsException if the user already exists.
     */
    fun createUser(userCreation: UserCreation): Result<UserInfo> = result {

        transactionFactory.execute {

            if (userRepository.hasUser(userCreation.username))
                throw UserAlreadyExistsException(userCreation.username)

            val generatedToken = UUID.randomUUID().toString()
            val userID = userRepository.addUser(
                userCreation.username,
                generatedToken,
                userCreation.password_hash
            ) ?: error("Error creating the user!")

            UserInfo(userID, generatedToken)
        }

    }

    /**
     * Gets the [UserID] of the user with the given token.
     * @param userToken The user token.
     * @return [Result] with the [UserID] of the user.
     * @throws NotFoundAppException if the user is not found.
     */
    fun getUserIDFromToken(userToken: String): Result<UserID> = result {
        transactionFactory.execute {
            userRepository.getUserIDByToken(userToken)
                ?: throw NotFoundAppException("user")
        }
    }


}
