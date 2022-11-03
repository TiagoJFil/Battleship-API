begin;

create table if not exists Authors(
    name varchar(20) primary key,
    email varchar(255) constraint emailinvalid check(email ~* '^[A-Z0-9.%-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}$'),
    github varchar(255)
);

create table if not exists SystemInfo(
    version varchar(20)
);

create table if not exists "User" (
  id serial primary key,
  "name" varchar(20) unique not null,
  password varchar(200) not null,
  salt varchar(32) not null
);

create table if not exists token(
    token varchar(255) primary key,
    userID int,
    foreign key(userID) references "User"(id)
);

create table if not exists ShipRules(
    id serial primary key,
    fleetInfo jsonb
);

create table if not exists GameRules (
    id serial primary key,
    boardSide int,
    shotsPerTurn int,
    layoutDefinitionTimeout int,
    playTimeout int,
    shiprules int,
    foreign key(shiprules) references ShipRules(id)
);

create table if not exists Game (
    id serial primary key,
    rules int, foreign key(rules) references GameRules(id),
    "state" varchar(20) check ("state" like 'placing_ships' or "state" like 'playing' or "state" like 'finished' or "state" like 'cancelled'),
    turn int,
    player1 int, foreign key(player1) references "User"(id),
    player2 int, foreign key(player2) references "User"(id),
    lastUpdated timestamp default now(),
    foreign key(turn) references "User"(id)
);

create table if not exists WaitingLobby(
   id serial primary key,
   player1 int, foreign key(player1) references "User"(id),
   player2 int, foreign key(player2) references "User"(id),
   gameid int, foreign key(gameid) references Game(id)
);

create table if not exists Board (
    layout text,
    gameId int,
    userId int,
    primary key (gameId, userId),
    foreign key (gameId) references Game(id),
    foreign key (userId) references "User"(id)
);
commit;


