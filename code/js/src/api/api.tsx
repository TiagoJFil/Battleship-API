import { SirenEntity } from '../interfaces/hypermedia/siren';
import { IAuthInformation, IUserDTO } from '../interfaces/dto/user-dto';
import { Problem } from '../interfaces/hypermedia/problem';
import { ILobbyInformationDTO } from '../interfaces/dto/lobby-info-dto';
import { IStatisticsDTO } from '../interfaces/dto/statistics-dto';
import axios, { AxiosError } from 'axios';
import { IShipInfoDTO } from '../interfaces/dto/ships-info-dto';
import { IGameRulesDTO } from '../interfaces/dto/game-rules-dto';
import { IGameStateInfoDTO } from '../interfaces/dto/game-state-dto';
import { ShipInfo } from '../components/entities/ship-info';
import { ISquareDTO } from '../interfaces/dto/square-dto';
import { IBoardDTO } from '../interfaces/dto/board-dto';
import { ISystemInfoDTO } from '../interfaces/dto/system-info-dto';
import { IGamesListDTO } from '../interfaces/dto/user-games-dto';

interface AppAction{
    name: string;
    method: string;
    href: string;
}

interface AppLink{
    rel: string[];
    href: string;
}

const basePath = "/api/"
const apiURL = process.env.API_URL || `http://localhost:8090`
const baseUrl = `${apiURL}${basePath}`


axios.defaults.baseURL = baseUrl
axios.defaults.withCredentials = true
axios.defaults.headers['Content-Type'] = 'application/json'

export async function fetchLogin (username: string, password: string) : Promise< SirenEntity<IAuthInformation>> {
    const response = await axios({
        url :`user/login`,
        method: 'POST',
        data: JSON.stringify(
            {
                "username": username,
                "password": password
            } 
        )
    }).catchAsProblem()

    return response.data
}

export async function fetchRegister (username: string, password: string) : Promise<SirenEntity<IAuthInformation>> {
    const response = await axios({
        url: `user/`,
        method: 'POST',
        data: JSON.stringify(
            {
                "username": username,
                "password": password
            }
        )
        
    }).catchAsProblem()

    return response.data
}

export async function getLobby(id:number) : Promise< SirenEntity<any>> {
    const response = await axios({
        method: 'GET',
        url: `lobby/${id}`,
    }).catchAsProblem()

    return response.data
}
    
export async function joinQueue() : Promise< SirenEntity<ILobbyInformationDTO>> {
    const response = await axios({
        url: `lobby/`,
        method: 'POST',
    }).catchAsProblem()

    return response.data
}

export async function leavelobby(lobbyID : number) : Promise< SirenEntity<any>> {
    const response = await axios({
        url: `lobby/${lobbyID}`,
        method: 'DELETE',
    }).catchAsProblem()

    return response.data
}

export async function getStatistics(): Promise<SirenEntity<IStatisticsDTO>> {
    const response = await axios({
        url: `statistics/?embedded=true`,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function getSystemInfo() : Promise< SirenEntity<ISystemInfoDTO>> {
    const response = await axios({
        url: `systemInfo/`,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function placeShips(gameID: number, ships: IShipInfoDTO[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `game/${gameID}/placeShips`,
        method: 'POST',
        data: JSON.stringify({
            "shipInfo": [...ships]
        })
    }).catchAsProblem()

    return response.data
}

export async function getGameRules(gameID: number): Promise<SirenEntity<IGameRulesDTO>> {
    const response = await axios({
        url: `game/${gameID}/rules`,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function getGameState(gameID: number,embedded: boolean = false): Promise<SirenEntity<IGameStateInfoDTO>> {
    let embeddedQuery = embedded ? "?embedded=true" : ""
    const response = await axios({
        url: `game/${gameID}/state` + embeddedQuery,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function defineShipLayout(gameID: number, shipInfo: ShipInfo[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `game/${gameID}/layout-definition`,
        method: 'POST',
        data: JSON.stringify({
            "shipsInfo": shipInfo
        })
    }).catchAsProblem()

    return response.data
}

export async function getBoard(gameID: number, whichFleet: string): Promise<SirenEntity<IBoardDTO>> {
    const response = await axios({
        url: `game/${gameID}/fleet/${whichFleet}`,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function defineShot(gameID: number, shotsInfo: ISquareDTO[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `game/${gameID}/shots-definition?embedded=true`,
        method: 'POST',
        data: JSON.stringify({
            shots: shotsInfo
        })
    }).catchAsProblem()

    return response.data
}

export async function getUserGames(): Promise<SirenEntity<IGamesListDTO>> { //todo change
    const response = await axios({
        url: `games/?embedded=true`,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function getHome() : Promise< SirenEntity<any>> {
    const response = await axios({
        url: ``,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

export async function getUserHome() : Promise< SirenEntity<any>> {
    const response = await axios({
        url: `my/`,
        method: 'GET',
    }).catchAsProblem()

    return response.data
}

declare global{
    interface Promise<T> {
        catchAsProblem(): Promise<T>
    }
}

Promise.prototype.catchAsProblem = function() {
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

