

create table token{
    userID int foreign key references user(id),
    token varchar(255),
}

create table Email{
    userID int foreign key references user(id),
    email varchar(255) constraint email_invalid check(email ~* '^[A-Z0-9._%-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$') primary key,
}

create table "User" (
    id serial primary key,
    "name" varchar(20),
    "password" varchar(20),
)


create table Board {
    id serial primary key,
    layout varchar(255) --Problematico pq o tamanho da board varia
}


create table Player {
    id int foreign key references "User"(id),
    attackBoard int foreign key references Board(id),
    defenseBoard int foreign key references Board(id),
    game int foreign key references Game(id)
}


create table GameVariables{
    id serial primary key,
    nShots int,
    nTiles int,
}


--falta saber quem ganhou
create table Game {
    id serial primary key,
    variables int foreign key references GameVariables(id),
    "state" varchar(20) foreign key check ( "state" in ("Placing","Running","Ended")),
    
    player1 int foreign key references Player(id),
    player2 int foreign key references Player(id)
}

create view RunningGames as
    select "User".id , Game.id,
      from "User" join Player on "User".id = Player.id 
      join Game on Player.game = Game.id
    where Game."state" = 'Running';

--just the number of games played
create view Ranking as select "User".name,"User".id, count(*) as nGames from "User" join Game on "User".id = Game.player1 or "User".id = Game.player2 group by "User".id order by nGames desc;