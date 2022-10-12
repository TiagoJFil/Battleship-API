create table Password (
     id serial primary key,
     password varchar(20)
)

create table "User" (
    id serial primary key,
    "name" varchar(20),
    passwordID int,
    foreign key(passwordID) references Password(id)
)

create table token(
    userID int,
    token varchar(255),
    foreign key(userID) references "User"(id)
);

create table Board (
    id serial primary key,
    layout text
)

create table FleetComposition (
    id serial primary key,
    shipSize int,
    numOfShips int,
)


create table GameRules (
    id serial primary key,
    boardSide int,
    shotsPerTurn int,
    maxTimeToDefineLayout int,
    maxTimeToPlay int,
    fleetComposition int, foreign key(fleetComposition) references FleetComposition(id)
)

create table Game (
    id serial primary key,
    rules int, foreign key(rules) references GameRules(id),
    "state" varchar(20) check ( "state" like 'Placing' or "state" like 'Running' or "state" like 'Ended'),
    player1 int, foreign key(player1) references "User"(id),
    player2 int, foreign key(player2) references "User"(id),
    boardP1 int, foreign key(boardP1) references Board(id),
    boardP2 int, foreign key(boardP2) references Board(id),
    winner int, foreign key(winner) references "User"(id)
)

-------------------------------------------------------------------------------------------------------------------------
create view RunningGames as
    select "User".id , Game.id,
      from "User" join Player on "User".id = Player.id 
      join Game on Player.game = Game.id
    where Game."state" = 'Running';

--just the number of games played
create view Ranking as select "User".name,"User".id, count(*) as nGames from "User" join Game on "User".id = Game.player1 or "User".id = Game.player2 group by "User".id order by nGames desc;