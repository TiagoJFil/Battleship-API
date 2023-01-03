import * as React from 'react' 
import { useNavigate } from 'react-router-dom';
import * as api from '../api/api';
import { authServices } from '../api/auth';
import { CircularProgress } from '@mui/material';
import { SirenEntity } from '../interfaces/hypermedia/siren';
import { ILobbyInformationDTO } from '../interfaces/dto/lobby-info-dto';
import AnimatedModal from '../components/modal';
import { INITIAL_MODAL_STATE, ModalState } from './modal-state-config';
import { GameConstants } from '../constants/game';
import { AppRoutes } from '../constants/routes';
import { Typography } from "@mui/material";

export function Lobby() {

    const navigate = useNavigate();

    const [gameID, setGameID] = React.useState<number | null>(null);
    const [lobbyID, setLobbyID] = React.useState<number | null>(null);
    const cancelled = React.useRef(false);
    const isPolling = React.useRef(true);
    const [customModalState, setCustomModalState] = React.useState<ModalState>(INITIAL_MODAL_STATE)

    const tryCancelLobby = () => {
        if(lobbyID != null && gameID === null){
            console.log(isPolling.current)
            if(isPolling.current && !cancelled.current){
                console.log('Cancelling lobby')
                api.leavelobby(lobbyID)
                .then(() => cancelled.current = true)
            }
        }
    }

    const scheduleStartGame = (gameID: number ) => {
        setTimeout(() => {
            navigate(`${AppRoutes.BASE_GAME}/${gameID}/layout-definition`, { replace: true })
        }, GameConstants.NAVIGATE_DELAY_MS)
    }

    React.useEffect(() => { // Use effect to join the lobby

        if(!authServices.isLoggedIn()){
            navigate(AppRoutes.LOGIN, { replace: true })
            return
        }

        api.joinQueue()
        .then((lobbyInfo: SirenEntity<ILobbyInformationDTO>) => {
            isPolling.current = true
            const lobbyInfoDto = lobbyInfo.properties
            if(lobbyInfoDto.gameID !== null){
                isPolling.current = false
                setGameID(lobbyInfoDto.gameID)
                scheduleStartGame(lobbyInfoDto.gameID) // Passed as an argument because gameID may not be set yet
                return
            }

            setLobbyID(lobbyInfoDto.lobbyID)
        })
        .catch((err) => setCustomModalState({message: err.message, isOpen: true}))

        return () => {
            tryCancelLobby
        } 

    }, [])

    React.useEffect(() => { // Once Joined, poll for gameID

        if(lobbyID === null) return

        const intervalID = setInterval(() => {
            api.getLobby(lobbyID)
            .then((lobbyInfoDto: SirenEntity<ILobbyInformationDTO>) => {
                const gameID = lobbyInfoDto.properties.gameID
                if(gameID !== null){
                    isPolling.current = false
                    setGameID(gameID)
                    clearInterval(intervalID)
                    scheduleStartGame(gameID)
                }
            })

        }, GameConstants.STATE_FETCH_INTERVAL_MS)

        return () => {
            clearInterval(intervalID)
            tryCancelLobby()
        }

    }, [lobbyID])

    const textContainer = (text : string ) => (
        <div className='center-container'>
            <Typography variant="body1">{text}</Typography>
        </div>
    )
    return (
        <div className='page'>
            <Typography className='app-title' align='center' variant="h2">Lobby</Typography>
            {isPolling.current && textContainer("Waiting for other players...")}
            {!isPolling.current && textContainer("Game is starting...")}
            {isPolling.current && <div className='screen-centered'> 
              <CircularProgress size='6rem' />
            </div>
            }
            {!isPolling.current && <div className='screen-centered'> 
            <CircularProgress size='6rem' color="secondary" />
            </div>
            }
            
            <AnimatedModal
                message={customModalState.message}
                show={customModalState.isOpen}
                handleClose={() => navigate(AppRoutes.HOME, { replace: true })}
            />
        </div>
    )
}