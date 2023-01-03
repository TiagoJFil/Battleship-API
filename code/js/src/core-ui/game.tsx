import * as React from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Square } from '../components/entities/square'
import { Fleet } from '../components/entities/fleet'
import { GameView } from '../pages/game-view'
import { EmbeddedEntity, SirenEntity } from '../interfaces/hypermedia/siren'
import '../css/board.css'
import { defineShot, getBoard, getGameRules, getGameState } from '../api/api'
import { SquareType } from '../components/entities/square-type'
import { IBoardDTO, toBoard } from '../interfaces/dto/board-dto'
import { Board } from '../components/entities/board'
import { IGameStateInfoDTO } from '../interfaces/dto/game-state-dto'
import { getCookie, UID_COOKIE_NAME } from '../api/auth'
import { IGameRulesDTO } from '../interfaces/dto/game-rules-dto'
import { GameState } from '../components/entities/game-state'
import { ModalState, ModalMessages, INITIAL_MODAL_STATE } from '../core-ui/modal-state-config'
import AnimatedModal from '../components/modal'

const INTERVAL_TIME_MS = 1000

export function Game() {
    const navigate = useNavigate()
    let { gameID } = useParams()

    const userID = getCookie(UID_COOKIE_NAME)

    const validatedUserID = parseInt(userID)
    const validatedGameID = parseInt(gameID)

    const [currentPlayerBoard, setPlayerBoard] = React.useState<Board>(null)
    const [currentOpponentBoard, setOpponentBoard] = React.useState<Board>(null)
    const [shotsDefinitionTimeout, setShotsDefinitionTimeout] = React.useState(null)
    const [turn, setTurn] = React.useState<Fleet>(null)
    const [customModalState, setCustomModalState] = React.useState<ModalState>(INITIAL_MODAL_STATE)
    const [timerResetToggle, setTimerResetToggle] = React.useState(false)

    const loading = currentPlayerBoard === null || currentOpponentBoard === null || shotsDefinitionTimeout === null;
    
    React.useEffect(() => {
        const getGameInfo = async () => {
            try{
                const gameStateResponse: SirenEntity<IGameStateInfoDTO> = await getGameState(validatedGameID)
                const gameStateDTO = gameStateResponse.properties
                const gameState = GameState[gameStateDTO.state]

                if(gameState === GameState.PLACING_SHIPS){
                    navigate(`/game/${validatedGameID}/layout-definition`)
                    return
                }else if(gameState !== GameState.PLAYING){
                    const message = gameState === GameState.FINISHED ? ModalMessages.Finished : ModalMessages.Cancelled
                    const newModalState: ModalState = {message, isOpen: true} 
                    setCustomModalState(newModalState)
                }

                gameStateDTO.turnID === validatedUserID ? setTurn(Fleet.MY) : setTurn(Fleet.OPPONENT)
            }catch(error){
                Promise.reject(error)
            }

            const playerBoardResponse: SirenEntity<IBoardDTO> = await getBoard(validatedGameID, Fleet.MY)
            const opponentBoardResponse: SirenEntity<IBoardDTO> = await getBoard(validatedGameID, Fleet.OPPONENT)

            const playerBoardDTO = playerBoardResponse.properties 
            const opponentBoardDTO = opponentBoardResponse.properties 

            const gameRulesResponse: SirenEntity<IGameRulesDTO> = await getGameRules(validatedGameID)
            const gameRulesDTO = gameRulesResponse.properties
            setShotsDefinitionTimeout(gameRulesDTO.playTimeout)
            setPlayerBoard(toBoard(playerBoardDTO))
            setOpponentBoard(toBoard(opponentBoardDTO))
        }

        getGameInfo()
    }, [])


    const changeTurn = () => {
        setTurn((prevTurn) =>{
            return prevTurn === Fleet.MY ? Fleet.OPPONENT : Fleet.MY
        })
    };

    const onOpponentBoardSquareClicked = (squareClicked: Square) => {

        if(turn !== Fleet.MY) return   //TODO FIX

        const boardRepresentation = currentOpponentBoard.asMap()

        const squareID = squareClicked.toID()
        const squareType = boardRepresentation.get(squareID) ?? SquareType.WATER

        if(squareType !== SquareType.WATER) return

        const makeShot = async () => {
            const squareDTO = squareClicked.toDTO()
            const shots = [squareDTO]
            
            const sirenResponse = await defineShot(validatedGameID, shots)

            const embeddedEntity = sirenResponse.entities[0] as EmbeddedEntity<IBoardDTO>
            const embeddedBoardDTO = embeddedEntity.properties
        
            setOpponentBoard(toBoard(embeddedBoardDTO))

            const gameInfo = await getGameState(validatedGameID)
            const gameInfoDTO = gameInfo.properties

            if(gameInfoDTO.state === GameState.FINISHED){
                const winner = gameInfoDTO.turnID === validatedUserID ? Fleet.OPPONENT : Fleet.MY //Last to play
                alert(`Game finished! Winner is ${winner}`) //CHANGE TO MODAL
            }
           
            changeTurn()
        }
        
        makeShot()
    }

    React.useEffect(() => {
        if(loading || turn !== Fleet.OPPONENT) return
        
        const updatePlayerBoard = async() =>{
            const siren: SirenEntity<IBoardDTO> = await getBoard(validatedGameID, Fleet.MY)
            const boardDTO = siren.properties

            const boardChanged =  boardDTO.shots.length !== currentPlayerBoard.shots.length ||
                                  boardDTO.hits.length !== currentPlayerBoard.hits.length

            if(boardChanged){
                setPlayerBoard(toBoard(boardDTO))
                changeTurn()
                //clearInterval(intervalID)
            }
        }

        const intervalID = setInterval(() => {
            if(turn === Fleet.OPPONENT){
                updatePlayerBoard()
            }
            console.log(`Interval ${intervalID} getting playerboard...`)
        }, INTERVAL_TIME_MS)

        return () => clearInterval(intervalID)

    }, [turn])


    const onTimerTimeout = async () => {
        defineShot(validatedGameID, [])
        .finally(() => {
            setCustomModalState({message: ModalMessages.Cancelled, isOpen: true})
        })
    }

    return (
        <div>
            <GameView
                loading={loading}
                playerBoard={currentPlayerBoard}
                opponentBoard={currentOpponentBoard}
                onOpponentBoardSquareClick={onOpponentBoardSquareClicked}
                shotsDefinitionTimeout={shotsDefinitionTimeout}
                timerResetToggle={timerResetToggle}
                onTimerTimeout={onTimerTimeout}
            />
            <AnimatedModal
                message={customModalState.message}
                show={customModalState.isOpen}
                handleClose={() => navigate('/', {replace: true})}
            />
        </div>
    )
}