begin;

create table Authors(
                        name varchar(20) primary key,
                        email varchar(255) constraint emailinvalid check(email ~* '^[A-Z0-9.%-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}$'),
                        github varchar(255)
);

create table SystemInfo(
                           name varchar(20) primary key,
                           version varchar(20)
);


create table "User" (
                        id serial primary key,
                        "name" varchar(20) unique not null,
                        password varchar(20) not null
);

create table token(
                      token varchar(255) primary key,
                      userID int,
                      foreign key(userID) references "User"(id)
);

create table ShipRules(
                          id serial primary key,
                          fleetInfo jsonb
);

create table GameRules (
                           id serial primary key,
                           boardSide int,
                           shotsPerTurn int,
                           layoutDefinitionTimeout int,
                           playTimeout int,
                           shiprules int,
                           foreign key(shiprules) references ShipRules(id)
);



create table Game (
                      id serial primary key,
                      rules int, foreign key(rules) references GameRules(id),
                      "state" varchar(20) check ( "state" like 'placing_ships' or "state" like 'playing' or "state" like 'finished' or "state" like 'waiting_player'),
                      turn int,
                      player1 int, foreign key(player1) references "User"(id),
                      player2 int, foreign key(player2) references "User"(id),
                      winner int, foreign key(winner) references "User"(id),
                      foreign key(turn) references "User"(id)
);

create table Board (
                       layout text,
                       gameId int,
                       userId int,
                       primary key (gameId, userId),
                       foreign key (gameId) references Game(id),
                       foreign key (userId) references "User"(id)


);

create view GameView as
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
       b2.layout    as boardP2
from Game g
         left join Gamerules gr on g.rules = gr.id
         left join ShipRules sr on gr.shiprules = sr.id
         left join "User" u on g.player1 = u.id
         left join Board b1 on b1.userId = g.player1 and b1.gameId = g.id
         left join Board b2 on b2.userId = g.player2 and b2.gameId = g.id;

-----------------------------------------------------------------------------------------------------


create function getGameId() returns int
    language plpgsql
    as $$
declare
gameId int = 1;
begin
        if
((select max(id) from Game) is not null) then
select max(id)
from Game into gameId;
gameid
= gameid +1;
end if;
return gameId;
end;
$$;

------------------------------------------------------------------------

create
or replace function gameRulesExists(
    newBoardSide int, newShotsPerTurn int,
    newLayoutDefinitionTimeout int, mewPlayTimeout int,
    newShipRulesId int
) returns boolean
    language plpgsql
as $$
declare

begin

    if
(
select boardSide
from GameRules
where boardSide = newBoardSide
  and shotsPerTurn = newShotsPerTurn
  and layoutDefinitionTimeout = newLayoutDefinitionTimeout
  and playTimeout = mewPlayTimeout
  and shiprules = newShipRulesId ) is null then
        return false;
else
        return true;
end if;
end;
$$;
create
or replace function shipRulesExists(newFleetInfo jsonb) returns boolean
    language plpgsql
as $$
begin
    if
(
select fleetInfo
from ShipRules
where fleetInfo = newFleetInfo) is null then
        return false;
else
        return true;
end if;
end;
$$;



--------------------------------------------------------------------------------------------------



create
or replace function insertGameView()
    returns trigger
    language plpgsql
AS $$
    declare
gameId int;
        shipRulesId
int;
        gameRulesId
int;
begin
select getGameId()
into gameId;

if
new.player1 is not null and new.player2 is not null then
            insert into Board(layout,gameId,userId) values (new.boardP1,gameId,new.player1);
insert into Board(layout, gameId, userId)
values (new.boardP2, gameId, new.player2);
end if;

        if
(
select not shipRulesExists(new.shiprules) ) then
insert
into ShipRules(fleetInfo)
values (new.shiprules);
end if;

select id
from ShipRules
where fleetInfo = new.shiprules into shipRulesId;

if
(
select not gameRulesExists(new.boardside, new.shotsperturn, new.layoutdefinitiontimeout, new.playTimeout, shipRulesId))then
insert
into GameRules(boardSide, shotsPerTurn, layoutDefinitionTimeout, playTimeout, shiprules)
values (new.boardSide, new.shotsPerTurn, new.layoutDefinitionTimeout, new.playTimeout, shipRulesId);
end if;

select id
from GameRules
where boardSide = new.boardSide into gameRulesId;

insert into Game(id, rules, state, turn, player1, player2)
values (gameId, gameRulesId, new.state, new.turn, new.player1, new.player2);
return new;
end;
$$;

----------------------------------------------------------------------------------------------------------------------------

create
or replace function updateGameView()
    returns trigger
    language plpgsql
AS $$
    declare
gameRulesId int;
		shipRulesId
int;
begin

       if
new.player1 is not null and new.player2 is not null and old.boardP1 is null and old.boardP2 is null then
       		insert into Board(layout,gameId,userId) values (new.boardP1,new.id,new.player1);
insert into Board(layout, gameId, userId)
values (new.boardP2, new.id, new.player2);
else
update Board
set layout = new.boardP1
where gameId = new.id
  and userId = old.player1;

update Board
set layout = new.boardP2
where gameId = new.id
  and userId = old.player2;
end if;

update Game
set state   = new.state,
    turn    = new.turn,
    player1 = new.player1,
    player2 = new.player2
where id = old.id;

select id
from shiprules s
where s.fleetinfo = old.shiprules into shipRulesId;

update ShipRules
set fleetInfo = new.shiprules
where id = shipRulesId;

select rules
from Game
where id = old.id into gameRulesId;

update GameRules
set boardSide               = new.boardSide,
    shotsPerTurn            = new.shotsPerTurn,
    layoutDefinitionTimeout = new.layoutDefinitionTimeout,
    playTimeout             = new.playTimeout,
    shiprules               = shipRulesId
where id = gameRulesId;
return new;
end;
$$;






create
or replace trigger insertOnGameView instead of insert
on GameView for each row execute procedure insertGameView();


create
or replace trigger updateOnGameView
    instead of
update on GameView for each row execute procedure updateGameView();

commit;
-----------------------------------------------------------------------------------------------------------------------------------------

insert into "User"
values (1, 'AstroFredy', ''),
       (2, 'Mike', ''),
       (3, 'Matilde', ''),
       (4, 'PurplePapi', ''),
       (5, 'Biou', '');

insert into gamerules
values (1, 10, 3, 2000, 2000),
       (2, 10, 1, 2000, 2000);

insert into shiprules
values ('Russian', 2, 2, 1),
       ('Russian', 3, 1, 1),
       ('Russian', 5, 1, 1),
       ('Russian', 4, 1, 1),
       ('USN', 3, 2, 2),
       ('USN', 2, 1, 2),
       ('USN', 4, 1, 2),
       ('USN', 7, 1, 2);

insert into game
values (1, 1, 'Finished', 2, 1, 2, 2),
       (2, 1, 'Running', 3, 2, 3, null),
       (3, 2, 'Finished', 5, 2, 5, 5),
       (4, 1, 'Finished', 4, 1, 4, 4),
       (5, 2, 'Running', 1, 1, 2, null),
       (6, 2, 'Running', 2, 3, 2, null),
       (7, 1, 'Finished', 5, 1, 5, 5),
       (8, 2, 'Running', 4, 3, 4, null),
       (9, 1, 'Finished', 3, 3, 2, 2);

insert into board
values ('####BB##B##B', 1, 2),
       ('####BBBBB##B', 1, 1),
       ('#B##BB##B##B', 2, 2),
       ('##BBBB##BB#B', 2, 3),
       ('####BB##B##B', 3, 2),
       ('####BBB#B##B', 3, 5),
       ('####BBBBBB#B', 4, 1),
       ('####BB#BB##B', 4, 4),
       ('BB##BB##B##B', 5, 1),
       ('####BB##B##B', 5, 2),
       ('####BBBB###B', 6, 3),
       ('#B##BB##B##B', 6, 2),
       ('B#BBBB##BB#B', 7, 1),
       ('##BBBBBBB##B', 7, 2),
       ('####BBB#B##B', 8, 3),
       ('B###BBBBBB#B', 8, 4),
       ('#BBBBB#BB##B', 9, 3),
       ('BBB#BB##B##B', 9, 2);

---------------------------------------------------------------------

drop table board cascade;
drop table game;
drop table shiprules cascade;
drop table gamerules cascade;
drop table "User" cascade;
drop table token;
drop table systeminfo;
drop table authors;


SELECT gr.shotsperturn, gr.boardside, gr.layoutdefinitiontimeout, gr.playtimeout, s.shipsize, s.shipcount
FROM shiprules s
         join gamerules gr on s.gamerulesid = gr.id
where s.gamerulesid = 1
SELECT gr.shotsperturn,
       gr.boardside,
       gr.layoutdefinitiontimeout,
       gr.playtimeout,
       s.shipsize,
       s.shipcount,
       gr.shiprules
FROM shiprules s
         join gamerules gr on s.gamerulesid = gr.id
where s.gamerulesid = 1

select *
from game;
select *
from board b;
