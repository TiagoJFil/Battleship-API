package pt.isel.daw.battleship.controller.pipeline.exceptions

import org.springframework.http.HttpStatus
import pt.isel.daw.battleship.domain.GameRuleViolationException
import pt.isel.daw.battleship.domain.IllegalGameStateException
import pt.isel.daw.battleship.services.exception.*

val errorToStatusMap = mapOf(
    UserAlreadyExistsException::class to HttpStatus.CONFLICT,
    InvalidParameterException::class to HttpStatus.BAD_REQUEST,
    MissingParameterException::class to HttpStatus.BAD_REQUEST,
    InvalidRequestException::class to HttpStatus.BAD_REQUEST,
    NotFoundAppException::class to HttpStatus.NOT_FOUND,
    GameNotFoundException::class to HttpStatus.NOT_FOUND,
    UserNotFoundException::class to HttpStatus.NOT_FOUND,
    InternalErrorAppException::class to HttpStatus.INTERNAL_SERVER_ERROR,
    ForbiddenAccessAppException::class to HttpStatus.FORBIDDEN,
    UnauthenticatedAppException::class to HttpStatus.UNAUTHORIZED,
    TimeoutExceededAppException::class to HttpStatus.REQUEST_TIMEOUT,
)


val domainToAppExceptionMap = mapOf(
    IllegalGameStateException::class to InvalidRequestException::class,
    GameRuleViolationException::class to InvalidRequestException::class,
)