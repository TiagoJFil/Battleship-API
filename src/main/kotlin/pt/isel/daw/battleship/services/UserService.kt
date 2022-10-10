package pt.isel.daw.battleship.services

import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken
import pt.isel.daw.battleship.utils.services.generateUUId

class UserService(
    private val transactionFactory: TransactionFactory
) {

    /**
     * Verifies the parameters received and calls the function [UserRepository].
     * @param user [UserCreateInput] contains the data given by the user.
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(user: UserCreateInput): Pair<UserToken, UserID> {
        val name = user.name
        val passwordHash = user.password

        val userAuthToken = generateUUId()

        return transactionFactory.execute {
            val userID = it.usersRepository.addUser(name, userAuthToken, passwordHash)

            return@execute Pair(userAuthToken, userID)
        }
    }
}