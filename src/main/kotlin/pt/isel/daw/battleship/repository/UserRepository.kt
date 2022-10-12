package pt.isel.daw.battleship.repository


import pt.isel.daw.battleship.repository.jdbi.UserName
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserToken

interface UserRepository {
    fun getUsersRanking(): List<Pair<UserName, Int>>
    fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): ID
}