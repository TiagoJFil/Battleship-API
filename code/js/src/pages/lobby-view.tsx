import * as React from 'react' 
import { useNavigate } from 'react-router-dom';
import { getGameRules, getLobby, joinQueue, leavelobby } from '../api/api';
import { authServices } from '../api/auth';
import { redirect } from "react-router-dom";

export function Lobby() {
    const [isLoading, setIsLoading] = React.useState(true);
    const [gameID  , setGameID] = React.useState<number | null>(null);
    const [lobbyID, setLobbyID] = React.useState<number | null>(null); 
    const hasJoinedGame = React.useRef(false);
    const cancelled = React.useRef(false);

    const navigate = useNavigate();

    React.useEffect(() => {
       
        if(!authServices.isLoggedIn()){
            navigate('/login', { replace: true })
            return
        }

        if(gameID != null) {
            navigate(`/game/${gameID}/layout-definition`, { replace: true })
            return
        }

        if(lobbyID != null){
            const intervalID = setInterval(
                async () => {
                    const joined = await verifyIfOtherPlayerJoined(lobbyID) 
                    
                    if (joined) {
                        hasJoinedGame.current = true;
                    }
                    if(hasJoinedGame.current){
                        console.log("clearing interval")
                        clearInterval(intervalID);
                    }
                },
                  1500
              );
        }

        const verifyIfOtherPlayerJoined = async (lobbyID : number) => {
            if(cancelled.current) {
                console.log("cancelled")
                return true;
            }

            const lobbyInfo = await getLobby(lobbyID)
            if (lobbyInfo.properties.gameID) {
                setGameID(lobbyInfo.properties.gameID)
                setIsLoading(false)
                return true;
            }
            return false;
        }

        const joinAndPoolLobby = async () => {
            if(lobbyID == null){
                const lobbyInfo = await joinQueue()
                setLobbyID(lobbyInfo.properties.lobbyID);
            }
        }

        joinAndPoolLobby()

        return () => {
            if(lobbyID != null && gameID == null){
                if(!hasJoinedGame.current){
                    cancelled.current = true
                    leavelobby(lobbyID)
                }
            }
        }
    }, [lobbyID,gameID]); 

    return (
        <div>
            <h1>Lobby</h1>
            {isLoading && <div>Loading...</div>}
            {isLoading && <div>Waiting for other players...</div>}
            {!isLoading && <div>Game is starting...</div>}
        </div>
    )
}