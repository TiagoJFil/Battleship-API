create table Authors
(
    name  varchar(20) primary key,
    email varchar(255)
        constraint email_invalid check (email ~* '^[A-Z0-9._%-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$') ,
    github varchar(255)
);

create table SystemInfo
(
    name    varchar(20) primary key,
    version varchar(20)
);

create table "User"
(
    id       serial primary key,
    "name"   varchar(20),
    password varchar(20)
);

create table token
(
    userID int,
    token  varchar(255) primary key,
    foreign key (userID) references "User" (id)
);

create table GameRules
(
    id                      serial primary key,
    boardSide               int,
    shotsPerTurn            int,
    layoutDefinitionTimeout int,
    playTimeout             int
);

create table ShipRules
(
    fleetName   varchar(20),
    shipSize    int,
    shipCount   int,
    gameRulesID int,
    foreign key (gameRulesID) references GameRules (id),
    primary key (fleetName, shipSize)
);

create table Game
(
    id      serial primary key,
    rules   int,
    foreign key (rules) references GameRules (id),
    "state" varchar(20) check ("state" like 'placing_ships' or "state" like 'playing' or "state" like 'finished' or
                               "state" like 'waiting_player'),
    turn    int,
    foreign key (turn) references "User" (id)
);

create table Board
(
    layout text,
    gameId int,
    userId int,
    primary key (gameId, userId),
    foreign key (gameId) references Game (id),
    foreign key (userId) references "User" (id)
);