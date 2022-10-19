package pt.isel.daw.battleship.services

import java.security.MessageDigest

private val digest = MessageDigest.getInstance("SHA-512") // "SHA-512"

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

fun hashPassword(password: String): String = digest.digest(password.toByteArray()).toHex()
