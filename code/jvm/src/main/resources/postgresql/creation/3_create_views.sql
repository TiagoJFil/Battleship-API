create or replace view GameView as
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
       b2.layout    as boardP2,
       g.lastUpdated
from Game g
         left join Gamerules gr on g.rules = gr.id
         left join ShipRules sr on gr.shiprules = sr.id
         left join "User" u on g.player1 = u.id
         left join Board b1 on b1.userId = g.player1 and b1.gameId = g.id
         left join Board b2 on b2.userId = g.player2 and b2.gameId = g.id;

create or replace view WinnersView as

select ws.id as gameId , ws.winner , u.name from
    (
        select g.player2 as winner, g.id
              from game g
              where state = 'finished'
                and turn = g.player1
    union
        select g.player1 as winner, g.id
              from game g
              where state = 'finished'
                and turn = g.player2
    ) as ws
join "User" u on u.id = ws.winner;

CREATE OR REPLACE VIEW RankingView as

    select u.id as playerId, count(g.id) as totalGames ,count(w.gameId) as wins
    from "User" u
             left join WinnersView w on u.id = w.winner
        join game g on u.id = g.player1 or u.id = g.player2
    group by u.id, u.name
    order by wins desc;


create or replace function insertGameView()
    returns trigger
    language plpgsql
AS $$
declare
    newGameId int;
    shipRulesId int;
    gameRulesId int;
begin
    select getGameId()into newGameId;

    if(select not shipRulesExists(new.shiprules)) then
        insert into ShipRules(fleetInfo) values (new.shiprules);
    end if;

    select id from ShipRules where fleetInfo = new.shiprules into shipRulesId;

    if(select not gameRulesExists(new.boardside, new.shotsperturn, new.layoutdefinitiontimeout, new.playTimeout, shipRulesId)) then
        insert into GameRules(boardSide, shotsPerTurn, layoutDefinitionTimeout, playTimeout, shiprules)
        values (new.boardSide, new.shotsPerTurn, new.layoutDefinitionTimeout, new.playTimeout, shipRulesId) returning id into gameRulesId;
    else
        select id from GameRules
                  where boardSide = new.boardSide and shotsperturn = new.shotsPerTurn
                    and layoutdefinitiontimeout = new.layoutDefinitionTimeout
                    and playtimeout = new.playTimeout into gameRulesId;

    end if;

    insert into Game(id, rules, state, turn, player1, player2, lastUpdated)
    values (newGameId, gameRulesId, new.state, new.turn, new.player1, new.player2, new.lastUpdated);

    if new.player1 is not null and new.player2 is not null then
            insert into Board(layout,gameId,userId) values (new.boardP1,newGameId,new.player1);
            insert into Board(layout, gameId, userId) values (new.boardP2, newGameId, new.player2);
    end if;


    return new;
end;
$$;

create or replace function updateGameView()
    returns trigger
    language plpgsql
AS $$
declare
    gameRulesId int;
	shipRulesId int;
begin
    if new.player1 is not null and new.player2 is not null and old.boardP1 is null and old.boardP2 is null then
       insert into Board(layout,gameId,userId) values (new.boardP1,new.id,new.player1);
       insert into Board(layout, gameId, userId) values (new.boardP2, new.id, new.player2);
    else
       update Board
       set layout = new.boardP1
       where gameId = new.id and userId = old.player1;

       update Board
       set layout = new.boardP2
       where gameId = new.id and userId = old.player2;
    end if;

    update Game set state = new.state, turn= new.turn, player1 = new.player1, player2 = new.player2, lastUpdated = new.lastUpdated where id = old.id;

    select id from shiprules s where s.fleetinfo = old.shiprules into shipRulesId;

    update ShipRules set fleetInfo = new.shiprules where id = shipRulesId;

    select rules from Game where id = old.id into gameRulesId;

    update GameRules set boardSide = new.boardSide,
    shotsPerTurn                   = new.shotsPerTurn,
    layoutDefinitionTimeout        = new.layoutDefinitionTimeout,
    playTimeout                    = new.playTimeout,
    shiprules                      = shipRulesId
    where id = gameRulesId;
    return new;
end;
$$;

create
or replace trigger insertOnGameView instead of insert
on GameView for each row execute procedure insertGameView();

create
or replace trigger updateOnGameView instead of
update on GameView for each row execute procedure updateGameView();