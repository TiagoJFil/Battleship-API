# DAW project

## Introduction

This project is the backend for the battleship game. 

To build this project we used the following technologies:
- **database**: PostgreSQL 
- **server side**: spring boot framework with kotlin, jdbi

# Software organization

### API Specification
Authentication is needed on every endpoint that uses the `POST` method. This authorization is provided by supplying the bearer token in the `Authorization` header.


### App configuration

The app configuration is located in the `application.yml` file.

#### Database configuration

The database connection is configured using the following environment variable:

- `JDBC_DATABASE_URL` - the database url containing the db information using the following format:
 "jdbc:postgresql://**IP**:**PORT**/**DB_NAME**?user=**USERNAME**&password=**PASSWORD**"
 The default configuration is:
 "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"



## App layers


The app is divided in the following layers:
- **API layer**: responsible for handling the requests from the client.
- **Services layer**: responsible for the business logic.
- **Data layer**: responsible for the communication with the database.


### API layer

#### Spring pipeline


![SpringPipeline](https://user-images.githubusercontent.com/86708200/203870698-e1fa2faf-405d-46d6-8947-ba837e43e569.svg)

* Authentication interceptor

To handle the authorization necessary for some endpoints, we use a custom annotation called `Authentication`.
This annotation is used in any handler that requires Authentication, if the userID of the authorized user is needed it can be supplied by an argument of the type `UserID`.
This process can be achieved by using the `AuthenticationInterceptor` class, which is responsible for intercepting the request and checking if the given token is valid, if it is valid it will add the `UserID` to the request attributes.

* Info Filter 

The `Info filter` is responsible for logging the request information, this is done by filtering the request and logging the information to the console.

* Error Handler

This class handles the errors that occur, this is done by using spring annotations and overriding spring errorHandler functions to catch the exceptions that are thrown while processing the outcome of the request.
To be able catch the Spring Related Exceptions it was necessary to add the following option to the `application.yml` file:
```
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```

[Example](https://github.com/isel-leic-daw/2022-daw-leic52d-2022-daw-leic52d-g06/tree/main/code/jvm/src/main/kotlin/pt/isel/daw/battleship/controller/pipeline/exceptions/ErrorHandler.kt#L25) of an error handler using spring annotations:

```kotlin	
//ErrorHandler.kt

@ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException, servletRequest: ServletWebRequest): ResponseEntity<Problem> {
        val problem = Problem(
            ex.type?.let { URI(it) },
            ex.message,
            instance = servletRequest.request.requestURI.toString()
        )

        ex.printStackTrace()
        errorLogger.error(" $ex : ${problem.title} on ${servletRequest.contextPath}")
        return ResponseEntity.status(errorToStatusMap[ex::class] ?: HttpStatus.INTERNAL_SERVER_ERROR)
            .setProblemHeader()
            .body(problem)
    }
```    

This is where the appropriate response is returned.

To associate our app exceptions with the correct HTTP status code, we use the following [mapping](https://github.com/isel-leic-daw/2022-daw-leic52d-2022-daw-leic52d-g06/tree/main/code/jvm/src/main/kotlin/pt/isel/daw/battleship/controller/pipeline/exceptions/errors.kt#L6):

```kotlin
//errors.kt

val errorToStatusMap = mapOf(
    UserAlreadyExistsException::class to HttpStatus.CONFLICT,
    InvalidParameterException::class to HttpStatus.BAD_REQUEST,
    MissingParameterException::class to HttpStatus.BAD_REQUEST,
    NotFoundAppException::class to HttpStatus.NOT_FOUND,
    GameNotFoundException::class to HttpStatus.NOT_FOUND,
    UserNotFoundException::class to HttpStatus.NOT_FOUND,
    InternalErrorAppException::class to HttpStatus.INTERNAL_SERVER_ERROR,
    ForbiddenAccessAppException::class to HttpStatus.FORBIDDEN,
    UnauthenticatedAppException::class to HttpStatus.UNAUTHORIZED,
    TimeoutExceededAppException::class to HttpStatus.REQUEST_TIMEOUT,


```

* Siren Content-type response advice
  
This advice is responsible for adding the siren content type to the response headers.
This only affects the responses that return a `SirenEntity`.

#### Hypermedia

##### Problem

##### Siren

###### Siren relationship graph 


![sirenRelationShip](https://user-images.githubusercontent.com/86708200/199119090-85b06f39-7add-48af-bdd8-c520f795b56d.svg)
All the api requests are followed by the base url: http://{adress}:{port}/api/

With the assist of the Siren specification, we can create a relationship graph of the api.
The graph is shown above.
The graph shows the relationship between the different entities and the actions that can be performed on them.
The Siren media returned by each endpoint also shows the different fields that are required for each neighbour action.
This can be used to get a better understanding of the api and how the different entities are related to each other.

### Service layer

### Data layer

#### Modeling the database


##### Conceptual model

##### Physical Model
The physical model of the database is available [here](https://github.com/isel-leic-daw/2022-daw-leic52d-2022-daw-leic52d-g06/tree/main/code/jvm/src/main/resources/postgresql/creation).
 

 # TODO UPDATE THE IMAGE
![image](https://user-images.githubusercontent.com/86708200/199119486-8293ef74-5986-46d9-8a55-e60c64903bf8.png)

### Data Access

To access the database we use the [JDBI](https://jdbi.org/) framework.
This framework gives us two choices about how we want to interact with the database. Its possible to use the `Fluent API` or the `Declarative API`. We chose to use the `Fluent API` because it is very similar to [JDBC](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/).

To reduce the complexity of the SQL queries and make them more readable on the data layere, we created our own JDBI mappers and on the database we created views and triggers to complement ******************************* (ver se ta bem complementar) the views.

//insert example of view and trigger and mapper
 
 

----

### Transaction Management

To always maintain a consistent state of the database, the service layer has to have support for transactions.

This feature required the introduction of new domain classes.

- **Transaction**: Represents a transaction that can be used to execute a series of data access operations.

- **TransactionFactory**: Creates a new transaction.

- **TransactionScope**: Provides the data access operations to be executed in a transaction.

#### Transaction

```kotlin
/**
 * Represents an app transaction.
 */
sealed interface Transaction {
    
    val scope: TransactionScope
    
    fun begin(level: IsolationLevel)
    
    fun commit()
    
    fun rollback()
    
    fun end()
}

```
The interface has these four trivial methods that are used to manage the state of the transaction.

A transaction may be executed with an isolation level.

#### TransactionFactory

This domain class is responsible for creating a new transaction.

```kotlin

interface TransactionFactory {
    fun getTransaction(): Transaction
}

```

Example of a correct transaction usage:
```kotlin
fun transactionExample(transactionFactory: TransactionFactory) {
    val transaction = transactionFactory.getTransaction()
    transaction.begin()
    try {
        // do something
    } catch (e: Exception) {
        transaction.rollback()
        throw e
    } finally {
        transaction.end()
    }
}

```

Using kotlin higher order functions allowed the creation of an abstraction
of the transaction management mechanism.


```kotlin
// Transaction.kt

fun <T> execute(block: TransactionScope.() -> T): T {
    begin()
    try {
        val result = this.scope.block()
        commit()
        return result
    } catch (e: Exception) {
        rollback()
        throw e
    } finally {
        end()
    }
}
```
All the database operations are wrapped in an [execute](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/main/src/main/kotlin/pt/isel/ls/utils/repository/transactions/Transaction.kt#L40) block.

e.g execute being used to create a sport.

```kotlin
// SportService.kt

fun createSport(token: UserToken?, name: String?, description: String?): SportID {
    logger.traceFunction(::createSport.name) { listOf(NAME_PARAM to name, DESCRIPTION_PARAM to description) }

    return transactionFactory.getTransaction().execute {

        val userID = usersRepository.requireAuthenticated(token) // db access
        val safeName = requireParameter(name, NAME_PARAM)
        val handledDescription = description?.ifBlank { null }

        sportsRepository.addSport(safeName, handledDescription, userID) // db access
    }
}

```

#### TransactionScope

This domain class is responsible for providing
the data access operations to be executed in a transaction.

Is the receiver of the execute function parameter `block`.

Is defined as follows:

```kotlin
sealed class TransactionScope(val transaction: Transaction) {
    abstract val sportsRepository: SportRepository
    abstract val routesRepository: RouteRepository
    abstract val activitiesRepository: ActivityRepository
    abstract val usersRepository: UserRepository
}
```

This implementation restricts the access to the repositories inside the transaction.
Which means the database is always in a consistent state.

As mentioned before, the data access is made through the JDBC library.

One possible implementation of transaction management for JDBC:

```kotlin
class JDBCTransaction(val connection: Connection) : Transaction {

    override val scope = JDBCTransactionScope(this)
    
    override fun begin() {
        connection.autoCommit = false
    }
    
    override fun commit() {
        connection.commit()
    }
    
    override fun rollback() {
        connection.rollback()
    }
    
    override fun end() {
        connection.autoCommit = true
    }
    
    override fun <T> execute(block: TransactionScope.() -> T): T {
        return connection.use { super.execute(block) }
    }
}

```

Each JDBC transaction is associated with a connection, which means that all database operations that are executed in this transaction
have to use the same connection.
This was accomplished by passing the connection to the respective scope and then to the respective repositories, provided by the same scope.
A database operation may not use all the repositories provided by the scope.
With this in mind all the repository objects are created lazily, so that the scope does not create them until they are needed.


----

### Error Handling Processing



----

# Web User Interface

## Routing

## Critical Evaluation

### Defects
### Improvements to be made
