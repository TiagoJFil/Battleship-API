import { SirenEntity } from '../hypermedia/siren';
import { AuthInformation } from '../entities/user';
import { Problem } from '../hypermedia/problem';
import axios from 'axios';
import { getAuthInfo } from './session';
import { LobbyInformation } from '../entities/lobbyInfo';
import { Statistics } from '../entities/statistics';

const hostname = "localhost"
const port = 8090
const basePath = "/api/"

const baseUrl = `http://${hostname}:${port}${basePath}`

export async function fetchLogin (username: string, password: string) : Promise< SirenEntity<AuthInformation>> {
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

export async function fetchRegister (username: string, password: string) : Promise< SirenEntity<AuthInformation>> {
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
            'Authorization': `Bearer ${getAuthInfo().token}`
        }
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}
    
export async function joinQueue() : Promise< SirenEntity<LobbyInformation>> {
    const response = await axios({
        url: `${baseUrl}lobby/`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthInfo().token}`
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
            'Authorization': `Bearer ${getAuthInfo().token}`
        }
    })
    .catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}

export async function getStatistics(): Promise<SirenEntity<Statistics>> {
    const response = await axios({
        url: `${baseUrl}statistics/`,
        method: 'GET',
        
    }).catch((e) => {
        throw e.response.data as Problem
    })

    return response.data
}