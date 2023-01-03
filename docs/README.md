# DAW project

## Introduction

This project is the backend for the battleship game. 

To build this project we used the following technologies:
- **database**: PostgreSQL 
- **server side**: Spring Boot framework with Kotlin and jdbi

# Software organization

### API Specification
Authentication is required for all endpoints that uses the `POST` method. This can be provided by sending the auth cookie in the `Cookie` header that is supplied by the login and register endpoints. 


### App configuration

The app configuration can be found in the `application.yml` file.

#### Database configuration

The database connection is configured using the following environment variable:

- `JDBC_DATABASE_URL` - The URL for the database, including IP, port, database name, username and password.
 The default configuration is:
 "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"


## App layers


The app is divided in the following layers:
- **API layer**: Handles requests from the client.
- **Services layer**: Containes the business logic.
- **Data layer**: Handle communication with the database.


### API layer

#### Spring pipeline



![SpringPipeline](https://user-images.githubusercontent.com/86708200/203870698-e1fa2faf-405d-46d6-8947-ba837e43e569.svg)

* Authentication interceptor

Handles authorization for endpoints.This is done with the `Authentication` annotation.
This annotation is used in any handler that requires Authentication, if the userID of the authorized user is needed it can be supplied with an argument of the type `UserID` and named `UserID` both of this conditions have to be met for it to be supplied.
This process can be achieved by using the `AuthenticationInterceptor` class, which is responsible for intercepting the request and checking if the given token is valid, if it is valid it will add the `UserID` to the request attributes and allow the request to complete sucessfully.

* Info Filter 

The `Info filter` is responsible for logging the request information, this is done by filtering the request and logging the information to the console.

* Error Handler

This class handles the errors that occur, this is done by using spring annotations and overriding spring errorHandler functions to catch the exceptions that are thrown while processing the outcome of the request.
To be able catch the Spring Related Exceptions.
In order to catch Spring-related exceptions the following options must be added to the application.yml file:


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
    InvalidRequestException::class to HttpStatus.BAD_REQUEST,
    NotFoundAppException::class to HttpStatus.NOT_FOUND,
    GameNotFoundException::class to HttpStatus.NOT_FOUND,
    UserNotFoundException::class to HttpStatus.NOT_FOUND,
    InternalErrorAppException::class to HttpStatus.INTERNAL_SERVER_ERROR,
    ForbiddenAccessAppException::class to HttpStatus.FORBIDDEN,
    UnauthenticatedAppException::class to HttpStatus.UNAUTHORIZED,
    TimeoutExceededAppException::class to HttpStatus.REQUEST_TIMEOUT,
)


```

* Siren Content-type response advice
  
This advice is responsible for adding the siren content type to the response headers.
This only affects the responses that return a `SirenEntity`.

#### Hypermedia

##### Problem

##### Siren
//  optionalHrefExpand , if the link or action has optionalHrefExpand = true, it will be added to the entity even if there are no placeholders to replace,link will be on the following format, href: /api/v1/endpoint/:id

###### Siren relationship graph 


![sirenRelationShip](https://user-images.githubusercontent.com/86708200/199119090-85b06f39-7add-48af-bdd8-c520f795b56d.svg)
All the api requests are followed by the base url: http://{adress}:{port}/api/

With the assist of the Siren specification, we can create a relationship graph of the api.
The graph is shown above.
The graph shows the relationship between the different entities and the actions that can be performed on them.
The Siren media returned by each endpoint also shows the different fields that are required for each neighbour action.
This can be used to get a better understanding of the api and how the different entities are related to each other.

### Service layer

#### 

### Data layer

#### Modeling the database

##### Physical Model
The physical model of the database is available [here](https://github.com/isel-leic-daw/2022-daw-leic52d-2022-daw-leic52d-g06/tree/main/code/jvm/src/main/resources/postgresql/creation).
 

 # TODO UPDATE THE IMAGE
![image](https://user-images.githubusercontent.com/86708200/199119486-8293ef74-5986-46d9-8a55-e60c64903bf8.png)

### Data Access

To access the database we use the [JDBI](https://jdbi.org/) framework.
This framework gives us two choices about how we want to interact with the database. Its possible to use the `Fluent API` or the `Declarative API`. We chose to use the `Fluent API` because it is very similar to [JDBC](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/).

To reduce the complexity of the SQL queries and make them more readable on the data layer, we created our own JDBI mappers and on the database we created views and triggers to complement  the views.


##### Mappers

We have created a mapper for the `ShipRules` entity so that this entity would be stored in JSON format and be parsed to JSON when inserted in the database.

```kotlin
//ShipRulesMapper.kt

class ShipRulesMapper: ColumnMapper<GameRules.ShipRules> {
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): GameRules.ShipRules {
        val obj = rs.getObject(columnNumber, PGobject::class.java)
        return JdbiGamesRepository.deserializeShipRulesFromJson(obj.value ?: throw IllegalStateException("Not a valid value"))
    }
}
```

##### Views

We have created a view in the database to easily map a GameDTO object, which contains all the necessary information to store a game. In order to handle the input and output of this view, we have created triggers for inserting and deleting records from the view. These triggers ensure that the data in the view stays up to date and consistent with the underlying tables.

To create this view we used the following query:

```sql
create or replace view GameView as
select g.id,
       gr.boardSide,
       gr.shotsPerTurn,
       gr.layoutDefinitionTimeout,
       gr.playTimeout,
       sr.fleetInfo as shiprules,
       g.state,
       g.turn,
       g.player1,
       g.player2,
       b1.layout    as boardP1,
       b2.layout    as boardP2,
       g.lastUpdated
from Game g
         left join Gamerules gr on g.rules = gr.id
         left join ShipRules sr on gr.shiprules = sr.id
         left join "User" u on g.player1 = u.id
         left join Board b1 on b1.userId = g.player1 and b1.gameId = g.id
         left join Board b2 on b2.userId = g.player2 and b2.gameId = g.id;
```

We have also created a view for the `Statistics` entity. 

##### Non Trivial Database SQL Statements


In order to get the games that are out of time, we created a function that returns a set of game ids.
The games can be timed out by two different timeouts, the `layoutDefinitionTimeout` and the `playTimeout`.
The `layoutDefinitionTimeout` is the time that a player has to place all the ships on the board.
The `playTimeout` is the time that a player has to make a move.
In order to verify if a game is timed out, we get the timeout value from the database and compare it to the time that has passed since the last update of the game.
If the time that has passed is greater than the timeout, the game is timed out.

```sql
create function getTimedOutGames() returns setof integer as $$
declare
    Vgameid integer;
    Vtimeout integer;
    Vlastupdated timestamp;
    VgameState varchar;
    now timestamp;
    outofplaytimeout boolean;
begin
    for Vgameid, VgameState in select id, state from gameview loop
            if VgameState = 'playing' then
                select playtimeout into Vtimeout from gameview where id = Vgameid;
            end if;
            if VgameState = 'placing_ships' then
                select layoutdefinitiontimeout into Vtimeout from gameview where id = Vgameid;
            end if;

            select lastupdated into Vlastupdated from gameview where id = Vgameid;
            select now()::timestamp into now;
            select age(now, Vlastupdated::timestamp + to_interval((Vtimeout / (1000* 60)))) >= '0 seconds'::interval into outofplaytimeout;

            if outofplaytimeout then
                return next Vgameid;
            end if;

        end loop;

    return;
end;
$$ language plpgsql;
```
This functions is then called by a procedure that updates the state of the timed out games to `cancelled`.

```sql

create procedure CancelOutOfTimeoutGames() as $$
begin
    update game set state = 'cancelled' where id in (select * from getOutOfTimeoutGames());
end;
$$ language plpgsql;
```
This procedure is needed because to check if a game is timed out, a user needs to make a request to the respective endpoint (makeShot or placeShip). if the user does not make a request, the game will never be timed out. The database stays inconsistent with the state of the game.
In order to solve this problem, we created a procedure that is called every 5 minutes by a spring worker.
//TODO: move this make a link to spring worker

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

fun <R> execute(block: Transaction.() -> R): R {
    return jdbi.inTransaction<R, Exception> { handle ->
        try {
            val transaction = JdbiTransaction(handle)
            block(transaction)
        } catch (e: Exception) {
            //handle exceptions ..
            throw InternalErrorAppException()
        }
    }
}

```
All the database operations are wrapped in an [execute](TODO: insertlink) block.

e.g execute being used to get a game State.

```kotlin
// GameService.kt

fun getGameState(gameID: ID, userID: UserID): GameStateInfo {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            if(userID !in game.playerBoards.keys)
                throw ForbiddenAccessAppException("User $userID is not part of the game $gameID")

            GameStateInfo(
                game.state,
                game.turnID,
                game.playerBoards.keys.first(),
                game.playerBoards.keys.last()
            )
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

The top level `App exceptions` are dealt with the Error Processing previously mentioned in the [spring pipeline](#Spring-Pipeline) section

The Domain exceptions are only thrown in the domain where the logic of the battleship is made.
These exceptions are caught in the Transaction `execute` function and are associated with its corresponded `App Exception` using the following map: 

```kotlin

val domainToAppExceptionMap = mapOf(
    IllegalGameStateException::class to InvalidRequestException::class,
    GameRuleViolationException::class to InvalidRequestException::class,
)

```


 



----

# Web User Interface


### Handling Authorization

The frontend handles authorization easily due to the way the backend handles it. The frontend sends the received cookie to the backend with each API request, and the backend returns the cookie to the frontend. This ensures that the frontend always has the most recent cookie for making requests. To remove the cookie from the frontend, we can use the document.cookie property, which is a string containing all the cookies for the current domain. By setting the expire date of the cookie to a date in the past, the browser will automatically remove the cookie because it recognizes that the expiry date has passed.


### Custom hooks

Use of custom hooks allows us to reuse code and keep our components clean and easy to read. The custom hooks are defined in the `src/hooks` directory. These are defined as follows:
//ir ao site q o costa mandou 
```typescript

```

###


## Critical Evaluation

### Defects
### Improvements to be made
