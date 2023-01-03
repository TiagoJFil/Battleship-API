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

export function Lobby() {

    const navigate = useNavigate();

    const [gameID, setGameID] = React.useState<number | null>(null);
    const [lobbyID, setLobbyID] = React.useState<number | null>(null);
    const cancelled = React.useRef(false);
    const [customModalState, setCustomModalState] = React.useState<ModalState>(INITIAL_MODAL_STATE)

    const isPolling = gameID === null

    const tryCancelLobby = () => {
        if(lobbyID != null && gameID === null){
            if(!isPolling && !cancelled.current){
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
            const lobbyInfoDto = lobbyInfo.properties
            if(lobbyInfoDto.gameID !== null){
                setGameID(lobbyInfoDto.gameID)
                scheduleStartGame(lobbyInfoDto.gameID) // Passed as an argument because gameID may not be set yet
                return
            }

            setLobbyID(lobbyInfoDto.lobbyID)
        })
        .catch((err) => setCustomModalState({message: err.message, isOpen: true}))

        return tryCancelLobby

    }, [])

    React.useEffect(() => { // Once Joined, poll for gameID

        if(lobbyID === null) return

        const intervalID = setInterval(() => {
            api.getLobby(lobbyID)
            .then((lobbyInfoDto: SirenEntity<ILobbyInformationDTO>) => {
                const gameID = lobbyInfoDto.properties.gameID
                if(gameID !== null){
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

    return (
        <div>
            <h1>Lobby</h1>
            {isPolling && <div>Waiting for other players...</div>}
            {!isPolling && <div>Game is starting...</div>}
            {isPolling && <CircularProgress />}
            {!isPolling && <CircularProgress color="secondary" />}
            <AnimatedModal
                message={customModalState.message}
                show={customModalState.isOpen}
                handleClose={() => navigate(AppRoutes.HOME, { replace: true })}
            />
        </div>
    )
}