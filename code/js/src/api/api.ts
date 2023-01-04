import { SirenEntity } from '../interfaces/hypermedia/siren';
import { IAuthInformation, IUserDTO } from '../interfaces/dto/user-dto';
import { Problem } from '../interfaces/hypermedia/problem';
import { ILobbyInformationDTO } from '../interfaces/dto/lobby-info-dto';
import { IStatisticsDTO } from '../interfaces/dto/statistics-dto';
import axios, { AxiosError } from 'axios';
import { IGameRulesDTO } from '../interfaces/dto/game-rules-dto';
import { IGameStateInfoDTO } from '../interfaces/dto/game-state-dto';
import { ShipInfo } from '../components/entities/ship-info';
import { ISquareDTO } from '../interfaces/dto/square-dto';
import { IBoardDTO } from '../interfaces/dto/board-dto';
import { ISystemInfoDTO } from '../interfaces/dto/system-info-dto';
import { IGamesListDTO } from '../interfaces/dto/user-games-dto';
import { ensureRelation, fillRelationsFromEntity, sendRelationRequest } from './requests';
import { GameTurn } from '../components/entities/turn';

const basePath = "/api"
const apiURL = process.env.API_URL || `http://localhost:8090`
const baseUrl = `${apiURL}${basePath}`

axios.defaults.baseURL = baseUrl
axios.defaults.withCredentials = true
axios.defaults.headers['Content-Type'] = 'application/json'

const fetchHome = async () => {

    const response = await axios({
        url: '/',
        method: 'GET'
    }).catchAndThrowAsProblem()

    fillRelationsFromEntity(response.data)

    return response.data
}

async function getUserHome(){
    const response = await axios({
        url: `my/`,
        method: 'GET',
    }).catchAndThrowAsProblem()

    fillRelationsFromEntity(response.data)

    return response.data
}

async function fetchAuth(username: string, password: string, authRel: string) : Promise< SirenEntity<IAuthInformation>> {

    const relation = await ensureRelation(authRel, fetchHome)

    return sendRelationRequest(relation, {
        username,
        password
    })

}

export async function fetchLogin (username: string, password: string) : Promise< SirenEntity<IAuthInformation>> {
    return fetchAuth(username, password, 'login')
}

export async function fetchRegister (username: string, password: string) : Promise<SirenEntity<IAuthInformation>> {
    return fetchAuth(username, password, 'register')
}

export async function getLobby(id: number) : Promise< SirenEntity<any>> {
    const relation = await ensureRelation('lobby-state')
    return sendRelationRequest(relation)
}
    
export async function joinQueue() : Promise< SirenEntity<ILobbyInformationDTO>> {
    const relation = await ensureRelation('queue', getUserHome)

    return sendRelationRequest(relation)
        .then((siren) => {
            fillRelationsFromEntity(siren)
            return siren
        })
}

export async function leavelobby(lobbyID : number) : Promise< SirenEntity<any>> {
    const relation = await ensureRelation('cancelQueue')
    return sendRelationRequest(relation)
}

export async function getStatistics(embedded: boolean = true): Promise<SirenEntity<IStatisticsDTO>> {
    const relation = await ensureRelation('statistics', fetchHome)
    console.log(relation)
    return sendRelationRequest(relation,  null, {embedded})
}

export async function getSystemInfo() : Promise< SirenEntity<ISystemInfoDTO>> {
    const relation = await ensureRelation('system-info', fetchHome)
    return sendRelationRequest(relation)
}

export async function getGameRules(gameID: number): Promise<SirenEntity<IGameRulesDTO>> {
    const relation = await ensureRelation('game-rules', async () => { await getGameState(gameID) })
    return sendRelationRequest(relation)
}

export async function getGameState(gameID: number, embedded: boolean=true): Promise<SirenEntity<IGameStateInfoDTO>> {
    const relation = {
        key: 'game-state',
        href: `game/${gameID}/state`,
        method: 'GET'
    }
    return sendRelationRequest(relation, null, {embedded})
}



export async function defineShipLayout(gameID: number, shipInfo: ShipInfo[]): Promise<SirenEntity<undefined>> {
    const relation = await ensureRelation('layout-definition', async () => { await getGameState(gameID) })
    return sendRelationRequest(relation, {shipsInfo: shipInfo})
}

export async function getBoard(gameID: number, whichFleet: string): Promise<SirenEntity<IBoardDTO>> {
    const relationKey = whichFleet === GameTurn.MY ? 'myFleet' : 'opponentFleet'
    const relation = await ensureRelation(relationKey, async () => { await getGameState(gameID) })
    return sendRelationRequest(relation)
}

export async function defineShot(gameID: number, shotsInfo: ISquareDTO[], embedded: boolean = true): Promise<SirenEntity<undefined>> {
    const relation = await ensureRelation('shots-definition', async () => { await getGameState(gameID) })
    return sendRelationRequest(relation, {shots: shotsInfo}, {embedded })
}

export async function getUserGames(embedded: boolean = true): Promise<SirenEntity<IGamesListDTO>> { //todo change
    const relation = await ensureRelation('user-games', getUserHome)
    return sendRelationRequest(relation, null, { embedded })
}

declare global{
    interface Promise<T> {
        catchAndThrowAsProblem(): Promise<T>
    }
}

Promise.prototype.catchAndThrowAsProblem = function() {
    return this.catch((e) => {
        if(e instanceof AxiosError) {
            throw {title: e.message, status: e.response.status} as Problem
        }
        const problem: Problem = e.response.data as Problem
        problem.status = e.response.status
        throw problem
    })
}

export {}

