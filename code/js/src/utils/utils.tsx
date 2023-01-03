import { getStatistics, getUserGames } from "../api/api";
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
        
        const getSelfLink = (link: SirenLink) => link.rel.includes('self');
        const embeddedInfo = fetchedStatistics.entities.find((entity: EmbeddedEntity<IUserDTO>) => {
            const uriProperties =  extractFromUri(entity.links.find(getSelfLink).href, userInfoURI)

            return entity.rel.includes(userNodeKey) && uriProperties.userID == playerStatistics.playerID;
        }) as EmbeddedEntity<IUserDTO>;
        
        delete newStats.playerID;
        newStats.playerName = embeddedInfo.properties.name;

        return newStats;
    });        
    stats.ranking = namedPlayerStats;
    return stats;
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

export function getSelfLink(link: SirenLink){
    return link.rel.includes('self');
} 