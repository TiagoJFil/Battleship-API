import { getGameState, getStatistics, getUserGames } from "../api/api";
import { IGameStateInfoDTO } from "../interfaces/dto/game-state-dto";
import { IPlayerStatisticsDTO, IStatisticsDTO } from "../interfaces/dto/statistics-dto";
import { IUserDTO } from "../interfaces/dto/user-dto";
import { EmbeddedEntity, SirenLink } from "../interfaces/hypermedia/siren";


/**
 * Extracts the strings between the : and / in the template
 * Could be one or multiple strings
 * Example: the given URI: /api/games/1 and the template: /api/games/:gameID 
 * will return the object {gameID: "1"}
 * 
 * @param uri The URI to extract the values from.
 * @param template The template to extract the values from.
 * @returns An object with the extracted values.
 */
export function extractFromUri(uri: string, template: string){
    const uriParts = uri.split('/');
    const templateParts = template.split('/');
    const extractedValues: any = {};
    for(let i = 0; i < uriParts.length; i++){
        if(templateParts[i].startsWith(':')){
            extractedValues[templateParts[i].substring(1)] = uriParts[i];
        }
    }
    return extractedValues;
}


export async function getStatisticsWithEmbeddedPlayers(){
    const fetchedStatistics = await getStatistics();

    let stats: IStatisticsDTO = fetchedStatistics.properties;
    const userNodeKey = 'user';
    const userInfoURI = fetchedStatistics.links.find((link: SirenLink) => link.rel.includes('user')).href;

    const namedPlayerStats = stats.ranking.map((playerStatistics: IPlayerStatisticsDTO) => {
        let newStats : any  = playerStatistics;
        
        const embeddedInfo = fetchedStatistics.entities.find((entity: EmbeddedEntity<IUserDTO>) => {
            return findUserWithId(playerStatistics.playerID, entity, userNodeKey, userInfoURI)
        }) as EmbeddedEntity<IUserDTO>;
        
        delete newStats.playerID;
        newStats.playerName = embeddedInfo.properties.name;

        return newStats;
    });        
    stats.ranking = namedPlayerStats;
    return stats;
}


export async function getGamesStateWithEmbeddedUsers(gameID : number){
    const fetchedState = await getGameState(gameID, true);
    const userNodeKey = 'user';
    const userInfoURI = fetchedState.links.find((link: SirenLink) => link.rel.includes(userNodeKey)).href;

    const User1ID = fetchedState.properties.player1ID 
    const User2ID = fetchedState.properties.player2ID

    const embeddedInfo1 = fetchedState.entities.find((entity: EmbeddedEntity<IUserDTO>) => {
         return findUserWithId(User1ID, entity, userNodeKey, userInfoURI) 
        }
    ) as EmbeddedEntity<IUserDTO>;
    
    const embeddedInfo2 = fetchedState.entities.find((entity: EmbeddedEntity<IUserDTO>) => {
        return findUserWithId(User2ID, entity, userNodeKey, userInfoURI) 
       }
    ) as EmbeddedEntity<IUserDTO>;

    const state = fetchedState.properties;
    return {
        stateInfo: state,
        player1: embeddedInfo1.properties,
        player2: embeddedInfo2.properties
    }
}


export async function getUserGamesWithEmbeddedState(){
    const fetchedGames = await getUserGames();
    const gameStateNodeKey = 'game-state';
    const gameStateUri = fetchedGames.links.find((link: SirenLink) => link.rel.includes(gameStateNodeKey)).href;

    const gamesWithState = fetchedGames.properties.values.map((gameID: number) => {

        const embeddedInfo = fetchedGames.entities.find((entity: EmbeddedEntity<IGameStateInfoDTO>) => {
            const uriProperties =  extractFromUri(entity.links.find(getSelfLink).href, gameStateUri)

            return entity.rel.includes(gameStateNodeKey) && uriProperties.gameID == gameID;
        }) as EmbeddedEntity<IGameStateInfoDTO>;

        return {
            gameID: gameID,
            state: embeddedInfo.properties
        };
    });        
    
    return gamesWithState;
}
function findUserWithId(userID : number,entity : EmbeddedEntity<IUserDTO>, userNodeKey : string, userInfoURI : string){
    const uriProperties =  extractFromUri(entity.links.find(getSelfLink).href, userInfoURI)

    return entity.rel.includes(userNodeKey) && uriProperties.userID == userID;
}

function getSelfLink(link: SirenLink){
    return link.rel.includes('self');
} 