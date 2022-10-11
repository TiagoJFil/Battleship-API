create table token{
    userID int foreign key references user(id),
    token varchar(255),
}

create table "User" (
    id serial primary key,
    "name" varchar(20),
    passwordID varchar(20) foreign key references Password(id)
)

create table Password (
     id serial primary key,
     password varchar(20)
)

create table Board {
    id serial primary key,
    layout text
}

create table Game {
    id serial primary key,
    rules int foreign key references GameRules(id),
    "state" varchar(20) foreign key check ( "state" in ("Placing","Running","Ended")),
    player1 int foreign key references Player(id),
    player2 int foreign key references Player(id),
    boardP1 int foreign key references Board(id)
    boardP2 int foreign key references Board(id),
    winner int foreign key references user(id)
}

create table GameRules {
    id serial primary key,
    boardSide int,
    shotsPerTurn int,
    maxTimeToDefineLayout int,
    maxTimeToPlay int,
    fleetComposition int foreign key references FleetComposition(id)
}

create table FleetComposition {
    id serial primary key,
    carrier int,
    battleship int,
    cruiser int,
    submarine int,
    destroyer int
}



create table Authors(
                        name varchar(20) primary key,
                        email varchar(255) constraint email_invalid check(email ~* '^[A-Z0-9._%-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$')
    github varchar(255)
)

create table SystemInfo(
                           name varchar(20) primary key,
                           version varchar(20)
)



-------------------------------------------------------------------------------------------------------------------------
create view RunningGames as
    select "User".id , Game.id,
      from "User" join Player on "User".id = Player.id 
      join Game on Player.game = Game.id
    where Game."state" = 'Running';

--just the number of games played
create view Ranking as select "User".name,"User".id, count(*) as nGames from "User" join Game on "User".id = Game.player1 or "User".id = Game.player2 group by "User".id order by nGames desc;