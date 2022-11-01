
## Build

To build the project

* Compile the project into a .jar
```
gradlew bootjar
```



## Running

* Start docker image with development time services
```
docker compose up --build --force-recreate 
```

or 

run the docker-compose.bat file