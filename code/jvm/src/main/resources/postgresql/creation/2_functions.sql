begin;

create or replace function getGameId() returns int
    language plpgsql
    as $$
declare
    gameId int = 1;
begin
    if((select max(id) from Game) is not null) then
        select max(id) from Game into gameId;
        gameid = gameid +1;
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

create or replace function shipRulesExists(newFleetInfo jsonb) returns boolean
    language plpgsql
as $$
begin
    if(
        select fleetInfo
        from ShipRules
        where fleetInfo = newFleetInfo
    ) is null then
        return false;
    else
        return true;
    end if;
end;
$$;


create or replace function to_interval(value int) returns interval as $$
select value * '1 minute'::interval;
$$ language sql;

create or replace function getOutOfTimeoutGames() returns setof integer as $$
declare
    Vgameid integer;
    Vtimeout integer;
    Vlastupdated timestamp;
    VgameState varchar;
    now timestamp;
    outofplaytimeout boolean;
begin

    for Vgameid, VgameState in select id, state from gameview loop
        if VgameState = 'playing' then
            select playtimeout into Vtimeout from gameview where id = Vgameid;
        end if;
        if VgameState = 'placing_ships' then
            select layoutdefinitiontimeout into Vtimeout from gameview where id = Vgameid;
        end if;

        select lastupdated into Vlastupdated from gameview where id = 18;
        select now()::timestamp into now;
        select age(now, Vlastupdated::timestamp + to_interval((Vtimeout / (1000* 60)))) >= '0 seconds'::interval
        into outofplaytimeout;

        if outofplaytimeout then
            return next Vgameid;
        end if;
    end loop;

    return;
end;
$$ language plpgsql;

create or replace procedure CancelOutOfTimeoutGames() as $$
begin
    update game set state = 'cancelled' where (state = 'playing' or state = 'placing_ships' )  and id in (select * from getOutOfTimeoutGames());
end;
$$ language plpgsql;

commit;