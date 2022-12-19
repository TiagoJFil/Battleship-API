import { SirenEntity } from '../interfaces/hypermedia/siren';
import { IAuthInformation, IUserDTO } from '../interfaces/dto/user-dto';
import { Problem } from '../interfaces/hypermedia/problem';
import { ILobbyInformationDTO } from '../interfaces/dto/lobby-info-dto';
import { IStatisticsDTO } from '../interfaces/dto/statistics-dto';
import axios from 'axios';
import { IShipInfoDTO } from '../interfaces/dto/ships-info-dto';
import { IGameRulesDTO } from '../interfaces/dto/game-rules-dto';
import { IGameStateInfoDTO } from '../interfaces/dto/game-state-dto';
import { ShipInfo } from '../components/entities/ship-info';
import { ISquareDTO } from '../interfaces/dto/square-dto';
import { IBoardDTO } from '../interfaces/dto/board-dto';
import { ISystemInfoDTO } from '../interfaces/dto/system-info-dto';

const hostname = "localhost"
const port = 8090
const basePath = "/api/"

const baseUrl = `http://${hostname}:${port}${basePath}`

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
    }).catch((e) => {
        throw e.response.data as Problem
    })

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
        
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getLobby(id:number) : Promise< SirenEntity<any>> {
    const response = await axios({
        method: 'GET',
        url: `lobby/${id}`,
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}
    
export async function joinQueue() : Promise< SirenEntity<ILobbyInformationDTO>> {
    const response = await axios({
        url: `lobby/`,
        method: 'POST',
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function leavelobby(lobbyID : number) : Promise< SirenEntity<any>> {
    const response = await axios({
        url: `lobby/${lobbyID}`,
        method: 'DELETE',
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getStatistics(): Promise<SirenEntity<IStatisticsDTO>> {
    const response = await axios({
        url: `statistics/`,
        method: 'GET',
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getSystemInfo() : Promise< SirenEntity<ISystemInfoDTO>> {
    const response = await axios({
        url: `systemInfo/`,
        method: 'GET',
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function placeShips(gameID: number, ships: IShipInfoDTO[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `game/${gameID}/placeShips`,
        method: 'POST',
        data: JSON.stringify({
            "shipInfo": [...ships]
        })
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getGameRules(gameID: number): Promise<SirenEntity<IGameRulesDTO>> {
    const response = await axios({
        url: `game/${gameID}/rules`,
        method: 'GET',
    }).catch((e) => {
        throw e.response.data as Problem
    })
    return response.data
}

export async function getGameState(gameID: number): Promise<SirenEntity<IGameStateInfoDTO>> {
    const response = await axios({
        url: `game/${gameID}/state`,
        method: 'GET',
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function defineShipLayout(gameID: number, shipInfo: ShipInfo[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `game/${gameID}/layout-definition`,
        method: 'POST',
        data: JSON.stringify({
            "shipsInfo": shipInfo
        })
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getBoard(gameID: number, whichFleet: string): Promise<SirenEntity<IBoardDTO>> {
    const response = await axios({
        url: `game/${gameID}/fleet/${whichFleet}`,
        method: 'GET',
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function defineShot(gameID: number, shotsInfo: ISquareDTO[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `game/${gameID}/shots-definition?embedded=true`,
        method: 'POST',
        data: JSON.stringify({
            shots: shotsInfo
        })
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}
