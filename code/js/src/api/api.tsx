import { SirenEntity } from '../interfaces/hypermedia/siren';
import { IAuthInformation } from '../interfaces/dto/user-dto';
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

const hostname = "localhost"
const port = 8090
const basePath = "/api/"

const baseUrl = `http://${hostname}:${port}${basePath}`
axios.defaults.withCredentials = true

export async function fetchLogin (username: string, password: string) : Promise< SirenEntity<IAuthInformation>> {
    const response = await axios({
        url :`${baseUrl}user/login`, 
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
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
        url: `${baseUrl}user/`, 
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
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
        url: `${baseUrl}lobby/${id}`,
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}
    
export async function joinQueue() : Promise< SirenEntity<ILobbyInformationDTO>> {
    const response = await axios({
        url: `${baseUrl}lobby/`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
          
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function leavelobby(lobbyID : number) : Promise< SirenEntity<any>> {
    const response = await axios({
        url: `${baseUrl}lobby/${lobbyID}`,
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getStatistics(): Promise<SirenEntity<IStatisticsDTO>> {
    const response = await axios({
        url: `${baseUrl}statistics/`,
        method: 'GET',
        
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function placeShips(gameID: number, ships: IShipInfoDTO[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `${baseUrl}game/${gameID}/placeShips`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
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
        url: `${baseUrl}game/${gameID}/rules`,
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    }).catch((e) => {
        throw e.response.data as Problem
    })
    return response.data
}

export async function getGameState(gameID: number): Promise<SirenEntity<IGameStateInfoDTO>> {
    const response = await axios({
        url: `${baseUrl}game/${gameID}/state`,
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function defineShipLayout(gameID: number, shipInfo: ShipInfo[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `${baseUrl}game/${gameID}/layout-definition`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
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
        url: `${baseUrl}game/${gameID}/fleet/${whichFleet}`,
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function defineShot(gameID: number, shotsInfo: ISquareDTO[]): Promise<SirenEntity<undefined>> {
    const response = await axios({
        url: `${baseUrl}game/${gameID}/shots-definition?embedded=true`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify({
            shots: shotsInfo
        })
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}