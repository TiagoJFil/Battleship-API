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

