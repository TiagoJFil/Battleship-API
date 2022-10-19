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
select g.id , gr.boardSide, gr.shotsPerTurn , gr.layoutDefinitionTimeout, gr.playTimeout ,
       sr.fleetInfo as shiprules,
       g.state ,
       g.turn,
       g.player1,
       g.player2,
       b1.layout as boardP1,
       b2.layout as boardP2
from Game g
         left join Gamerules gr on g.rules = gr.id
         left join ShipRules sr on gr.shiprules = sr.id
         left join "User" u on g.player1 = u.id
         left join Board b1 on b1.userId = g.player1 and b1.gameId = g.id
         left join Board b2 on b2.userId = g.player2 and b2.gameId = g.id;

create function getGameId() returns int
    language plpgsql
    as $$
    declare
        gameId int = 0;
    begin
        if ((select max(id) from Game) is not null) then
            select max(id) from Game into gameId;
        end if;
        return gameId;
    end;
$$;

create or replace function gameRulesExists(
    newBoardSide int, newShotsPerTurn int,
    newLayoutDefinitionTimeout int, mewPlayTimeout int,
    newShipRulesId int
) returns boolean
    language plpgsql
as $$
declare

begin

    if(
        select boardSide from GameRules where boardSide = newBoardSide
                                          and shotsPerTurn = newShotsPerTurn
                                          and layoutDefinitionTimeout = newLayoutDefinitionTimeout
                                          and playTimeout = mewPlayTimeout
                                          and shiprules = newShipRulesId
    ) is null then
        return false;
    else
        return true;
    end if;
end;
$$;
create or replace function shipRulesExists(newFleetInfo jsonb) returns boolean
    language plpgsql
as $$
begin
    if(select fleetInfo from ShipRules where fleetInfo = newFleetInfo) is null then
        return false;
    else
        return true;
    end if;
end;
$$;

create or replace function insertGameView()
    returns trigger
    language plpgsql
AS $$
    declare
        gameId int;
        shipRulesId int;
        gameRulesId int;
    begin
        select getGameId() into gameId;

        if new.player1 is not null and new.player2 is not null then
            insert into Board(layout,gameId,userId) values (new.boardP1,gameId,new.player1);
            insert into Board(layout,gameId,userId) values (new.boardP2,gameId,new.player2);
        end if;

        if( select not shipRulesExists() ) then
            insert into ShipRules(fleetInfo) values (new.shiprules);
        end if;

        select id from ShipRules where fleetInfo = new.shiprules into shipRulesId;

        if (select not gameRulesExists())then
                insert into GameRules(boardSide,shotsPerTurn,layoutDefinitionTimeout,playTimeout,shiprules)
                values (new.boardSide,new.shotsPerTurn,new.layoutDefinitionTimeout,new.playTimeout,shipRulesId);
        end if;

        select id from GameRules where boardSide = new.boardSide into gameRulesId;

        insert into Game(id,rules,state,turn,player1,player2)
        values (gameId,gameRulesId,new.state,new.turn,new.player1,new.player2);

    end;
$$;

create or replace function updateGameView()
    returns trigger
    language plpgsql
AS $$
    declare
        gameRulesId int;

    begin

        update Board
        set layout = new.boardP1
        where gameId = old.id and userId = old.player1;

        update Board
        set layout = new.boardP2
        where gameId = old.id and userId = old.player2;

        update Game
        set state = new.state,
            turn = new.turn,
            player1 = new.player1,
            player2 = new.player2
        where id = old.id;

        update ShipRules
        set fleetInfo = new.shiprules
        where id = old.shiprules;

        select rules from Game where id = old.id into gameRulesId;

        update GameRules
        set boardSide               = new.boardSide,
            shotsPerTurn            = new.shotsPerTurn,
            layoutDefinitionTimeout = new.layoutDefinitionTimeout,
            playTimeout             = new.playTimeout,
            shiprules               = new.shiprules
        where id = gameRulesId;

    end;
$$;



create or replace trigger insertOnGameView instead of insert
on GameView for each row execute procedure insertGameView();


create or replace trigger updateOnGameView instead of update
on GameView for each row execute procedure updateGameView();

commit;