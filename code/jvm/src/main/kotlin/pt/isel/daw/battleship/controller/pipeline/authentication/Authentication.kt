package pt.isel.daw.battleship.controller.pipeline.authentication


/**
 * Used on Rest Controller handlers to indicate that it requires authentication.
 *
 * This function can specify a parameter userID that will be injected with the authenticated user id.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Authentication





