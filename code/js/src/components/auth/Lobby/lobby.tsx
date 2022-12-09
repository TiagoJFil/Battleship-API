import * as React from 'react' 
import { useNavigate } from 'react-router-dom';
import { getGameRules, getLobby, joinQueue, leavelobby } from '../../../api/api';


export function Lobby() {
    const [isLoading, setIsLoading] = React.useState(true);
    const [gameID  , setGameID] = React.useState<number | null>(null);
    const [lobbyID, setLobbyID] = React.useState<number | null>(null); 
    const [gameRules, setGameRules] = React.useState(null);  
    const hasJoinedGame = React.useRef(false);
    const cancelled = React.useRef(false); // this cancelled could be a variable inside the useEffect

    const navigate = useNavigate();

    React.useEffect(() => {
        if(lobbyID != null){
            console.log("lobbyID is not null")

            const intervalID = setInterval(
                async () => {
                    console.log("retrying")
                    const joined = await verifyIfOtherPlayerJoined(lobbyID) 
                    
                    if (joined) {
                        hasJoinedGame.current = true;
                    }
                    console.log("hasResolved is", hasJoinedGame.current)
            
                    if(hasJoinedGame.current){
                        console.log("clearing interval")
                        clearInterval(intervalID);
                    }
                },
                  1500
              );
        }

        if(gameID != null && gameRules == null){
            getGameRules(gameID).then((gameRules) => {
                setGameRules(gameRules);
            })
        }

        if(gameRules != null){
            const intervalID = setInterval(() => {
                    navigate(`/game/${gameID}/layout-definition`, {state: gameRules.properties})
                    clearInterval(intervalID);
                },
                3000
            )
            
        }

        const verifyIfOtherPlayerJoined = async (lobbyID : number) => {
            if(cancelled.current) {
                console.log("cancelled")
                return true;
            }
            console.log("calling getLobby")
            const lobbyInfo = await getLobby(lobbyID)
            if (lobbyInfo.properties.gameID) {
                setGameID(lobbyInfo.properties.gameID)
                setIsLoading(false)
                return true;
            }
            return false;
        }
        //join queue    
        const joinAndPoolLobby = async () => {
            if(lobbyID == null){
                console.log("joining")
                const lobbyInfo = await joinQueue()
                setLobbyID(lobbyInfo.properties.lobbyID);
            }
        }

        joinAndPoolLobby()

        return () => {
            
            if(lobbyID != null && gameID == null){
                console.log("has a lobby")

                if(!hasJoinedGame.current){
                    console.log("LOG: leaving lobby")
                    cancelled.current = true
                    leavelobby(lobbyID)
                    
                }
            }
            // cleanup
        }
    }, [lobbyID,gameID, gameRules]); 

    return (
        <div>
            <h1>Lobby</h1>
            {isLoading && <div>Loading...</div>}
            {isLoading && <div>Waiting for other players...</div>}
            {!isLoading && <div>Game is starting...</div>}
        </div>
    )
}