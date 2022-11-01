package pt.isel.daw.battleship.services.model

import java.security.MessageDigest


private const val HASHING_ALGORITHM = "SHA-512"

private val digest = MessageDigest.getInstance(HASHING_ALGORITHM) // "SHA-512"

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

fun hashPassword(password: String): String = digest.digest(password.toByteArray()).toHex()