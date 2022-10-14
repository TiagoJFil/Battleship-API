begin transaction ;

create table "User" (
    id serial primary key,
    "name" varchar(20),
    password varchar(20)
);

create table token(
    userID int,
    token varchar(255) primary key,
    foreign key(userID) references "User"(id)
);

create table FleetComposition (
                                  fleetName varchar(20),
                                  shipSize int,
                                  numOfShips int,
                                  primary key (fleetName, shipSize)
);

create table GameRules (
                           id serial primary key,
                           boardSide int,
                           shotsPerTurn int,
                           layoutDefinitionTimeout int,
                           playTimeout int,
                           fleetComposition varchar(20),
                           foreign key(fleetComposition) references FleetComposition(fleetName)
);

create table Game (
                      id serial primary key,
                      rules int, foreign key(rules) references GameRules(id),
                      "state" varchar(20) check ( "state" like 'Placing' or "state" like 'Running' or "state" like 'Ended' or "state" like 'Waiting'),
                      turn int check (turn >= 1 and turn <= 2),
                      player1 int, foreign key(player1) references "User"(id),
                      player2 int, foreign key(player2) references "User"(id),
                      winner int, foreign key(winner) references "User"(id)
);


create table Board (
    layout text,
    gameId int,
    userId int,
    primary key (gameId, userId),
    foreign key (gameId) references Game(id),
    foreign key (userId) references "User"(id)
);


create table Authors(
    name varchar(20) primary key,
    email varchar(255) constraint email_invalid check(email ~* '^[A-Z0-9._%-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$'),
    github varchar(255)
);

create table SystemInfo(
       name varchar(20) primary key,
       version varchar(20)
);

commit ;
-------------------------------------------------------------------------------------------------------------------------
create view RunningGames as
    select "User".id , Game.id,
      from "User" join Player on "User".id = Player.id 
      join Game on Player.game = Game.id
    where Game."state" = 'Running';

--just the number of games played
create view Ranking as select "User".name,"User".id, count(*) as nGames from "User" join Game on "User".id = Game.player1 or "User".id = Game.player2 group by "User".id order by nGames desc;