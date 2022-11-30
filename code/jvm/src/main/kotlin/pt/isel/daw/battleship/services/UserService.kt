package pt.isel.daw.battleship.services

import org.springframework.stereotype.Service
import pt.isel.daw.battleship.repository.dto.UserDTO
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.services.exception.*
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.validationEntities.UserValidation
import pt.isel.daw.battleship.utils.UserID

@Service
class UserService(
    val transactionFactory: TransactionFactory
) {

    /**
     * Creates a new user.
     * @param userValidation The user creation information.
     * @return [Result] with the user's ID.
     * @throws UserAlreadyExistsException if the user already exists.
     * @throws InternalErrorAppException if an internal error occurs.
     */
    fun createUser(userValidation: UserValidation): AuthInformation =

        transactionFactory.execute {

            if (userRepository.hasUser(userValidation.username))
                throw UserAlreadyExistsException(userValidation.username)

            val generatedToken = generateUUId()

            val salt = generateSalt()
            val hashedPassword = hashPassword(userValidation.password, salt)

            val userID = userRepository.addUser(
                UserDTO(
                    name = userValidation.username,
                    token = generatedToken,
                    hashedPassword = hashedPassword,
                    salt = salt
                )
            ) ?: throw InternalErrorAppException()

            AuthInformation(userID, generatedToken)
        }


    /**
     * Verifies the user's credentials and returns the information need to perform authorized actions.
     * @param userValidation The user login information.
     * @return [AuthInformation] if the credentials are valid.
     * @throws UserNotFoundException if the user does not exist.
     * @throws InvalidParameterException if the password ou username is invalid.
     */
    fun authenticate(userValidation: UserValidation): AuthInformation =
        transactionFactory.execute {
            if (!userRepository.hasUser(userValidation.username))
                throw UserNotFoundException(userValidation.username)

            val salt = userRepository.getUserSalt(userValidation.username)

            val hashedAndSaltedPassword = hashPassword(userValidation.password, salt)

            userRepository.verifyUserCredentials(
                userValidation.username,
                hashedAndSaltedPassword
            ) ?: throw InvalidParameterException("Invalid username or password")
        }


    /**
     * Gets the [UserID] of the user with the given token.
     * @param userToken The user token.
     * @return [Result] with the [UserID] of the user.
     * @throws UnauthenticatedAppException if the user is not found.
     * @throws InternalErrorAppException if an internal error occurs.
     */
    fun getUserIDFromToken(userToken: String?): UserID {
        if (userToken.isNullOrEmpty()) {
            throw UnauthenticatedAppException()
        }
        return transactionFactory.execute {
            userRepository.getUserIDByToken(userToken)
                ?: throw UnauthenticatedAppException()
        }
    }

    /**
     * Gets the user information.
     * @param userID The user ID to get.
     * @return [User] with the user information.
     */
    fun getUser(userID: UserID): User {
        return transactionFactory.execute {
            userRepository.getUser(userID)
                ?: throw NotFoundAppException("User with ID $userID")
        }
    }


}
