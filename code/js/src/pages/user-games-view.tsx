import * as React from "react"
import { useNavigate } from 'react-router-dom'
import { authServices } from '../api/auth'
import ButtonList from "../components/button-list"
import { GameState } from "../components/entities/game-state"
import { GameButton } from "../components/game-button"
import { IGameStateInfoDTO } from "../interfaces/dto/game-state-dto"
import { getGamesStateWithEmbeddedUsers, getUserGamesWithEmbeddedState } from "../utils/utils"
import { Typography } from "@mui/material";
import "../css/user-games-view.css"
import { getUserGames } from "../api/api"
import { IUserDTO } from "../interfaces/dto/user-dto"
import { CircularProgress } from '@mui/material';


export function UserGames(){
    const navigate = useNavigate()

    const [gamesWithState, setGamesWithState] : [{ gameID: number, stateWithPlayers: {
        stateInfo: IGameStateInfoDTO;
        player1: IUserDTO;
        player2: IUserDTO;
    }}[] | null,any] = React.useState(null)
    const loading = gamesWithState === null

    const onGameClick = (event : any, gameID : number,gameInfo: IGameStateInfoDTO) => {
        event.preventDefault()
        const gameState = GameState[gameInfo.state]
        if(gameState === GameState.PLACING_SHIPS){
            navigate(`/game/${gameID}/layout-definition`)
        }
        else if(gameState === GameState.PLAYING){
            navigate(`/game/${gameID}`)
        }
    }

    React.useEffect(() => {
        if(!authServices.isLoggedIn()){
            navigate('/login', { replace: true }) 
            return
        }
 
        const fetchUserGamesInfo = async () => {
            const games = await getUserGames()
            const GamesStateWithUsers = await Promise.all(games.properties.values.map(async (gameID) => {
                const state = await getGamesStateWithEmbeddedUsers(gameID)
                return { gameID: gameID, stateWithPlayers: state }
            }))
            setGamesWithState(GamesStateWithUsers)
        }
        fetchUserGamesInfo()
    }, [])

    if(loading == true){
        return <div className='screen-centered'> 
                  <CircularProgress size='6rem' />
               </div>
    }

    if(gamesWithState.length === 0){
        return <div className='screen-centered'>
            <Typography variant='h4'>You don't have any games yet</Typography>
        </div>
    }

    const buttons = gamesWithState.map((it) => {
        return { info: it, onClick: (e) => onGameClick(e,it.gameID,it.stateWithPlayers.stateInfo) } 
    })

    return (
        <div className="page">
            <Typography className='app-title' align='center' variant="h2">My Games</Typography>
            <div className="center-container">
                <div className="ButtonContainer">
                    <ButtonList buttons={buttons} />  
                </div>
            </div>
        </div>
    )

}