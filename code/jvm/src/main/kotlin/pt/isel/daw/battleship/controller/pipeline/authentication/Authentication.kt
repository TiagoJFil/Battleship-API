package pt.isel.daw.battleship.controller.pipeline.authentication

import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.services.exception.UnauthenticatedAppException

/**
 * Used on Rest Controller handlers to indicate that it requires authentication.
 *
 * The handler annotated with [Authentication] can specify a parameter
 * that must be named userID and of type [UserID] to receive the user id.
 *
 * This userID will be injected by the [AuthenticationInterceptor] and [UserIDArgumentResolver].
 *
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Authentication





