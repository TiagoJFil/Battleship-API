# DAW project

## Introduction

This project is the backend for the battleship game. 

To build this project we used the following technologies:
- **database**: PostgreSQL 
- **server side**: spring boot framework with kotlin, jdbi and jackson


### Siren relationship graph ###

![sirenRelationShip](https://user-images.githubusercontent.com/86708200/199119090-85b06f39-7add-48af-bdd8-c520f795b56d.svg)
All the api requests are followed by the base url: http://{ip}:{port}/api/

With the assist of the Siren specification,
we can create a relationship graph of the api.
The graph is shown above.
The graph shows the relationship between the different entities and the actions that can be performed on them.
The Siren media returned by each endpoint also shows the different fields that are required for each neighbour action.
This can be used to get a better understanding of the api and how the different entities are related to each other.


### Physical Model ###

The physical model of the database is available [here](https://github.com/isel-leic-ls/2122-2-LEIC42D-G04/blob/code/jvm/src/main/resources/postgresql/creations).
 
![image](https://user-images.githubusercontent.com/86708200/199119486-8293ef74-5986-46d9-8a55-e60c64903bf8.png)


## Software Organization ##

Authentication is needed on `POST` operations. This authorization is provided by supplying the bearer token in the `Authorization` header.

### App layers
The app is divided in the following layers:
- **API layer**: responsible for handling the requests from the client.
- **Services layer**: responsible for the business logic.
- **Data layer**: responsible for the communication with the repository.


