import * as React from 'react'
import { useNavigate, useParams } from "react-router-dom";
import { emptyBoard, Board } from '../components/entities/board';
import { Orientation } from '../components/entities/orientation';
import { Ship } from '../components/entities/ship';
import { Square } from '../components/entities/square';
import { PlaceShipView } from '../pages/place-ships-view';
import { GameState } from '../components/entities/game-state';
import * as api from '../api/api';
import { ShipInfo } from '../components/entities/ship-info';
import { authServices } from '../api/auth';
import AnimatedModal from '../components/modal'
import { BoardControls } from '../components/board/board-view';
import { FleetControls, FleetState } from '../components/fleet/fleet-view';
import { IGameRulesDTO } from '../interfaces/dto/game-rules-dto';
import { INITIAL_MODAL_STATE, ModalMessages, ModalState } from './modal-state-config';
import { AppRoutes } from '../constants/routes';
import { GameTurn } from '../components/entities/turn';
import { toBoard } from '../interfaces/dto/board-dto';
import CircularProgress from '@mui/material/CircularProgress';
import { InfoToast, SuccessToast } from './toasts';
 
const RIGHT_MOUSE_CLICK_EVENT = 2
const INTERVAL_TIME_MS = 1000

interface LayoutDefinitionRules{
    ships: Ship[],
    boardSide: number,
    layoutDefinitionTimeout: number
}

export function PlaceShips(){
    const navigate = useNavigate();
    let { gameID } = useParams()

    const validatedGameID = parseInt(gameID)
    const shootingGamePhaseURL = `/game/${gameID}`

    const boardSnapshot = React.useRef<Board>(null) // Board used to store the valid state of the board at each placement
    const gameRules = React.useRef<LayoutDefinitionRules>(null)
    const [visibleBoard, setVisibleBoard] = React.useState(null) // Board that is displayed to the user
    const [shipSelected, setShipSelected] = React.useState(null) // Ship that is currently selected to be placed
    const [availableShips, setAvailableShips] = React.useState([]) // Ships that are available to be placed
    const [placedShips, setPlacedShips] = React.useState([]) // Ships that have been placed
    const [customModalState, setCustomModalState] = React.useState<ModalState>(INITIAL_MODAL_STATE)
    const [readyToPlay, setReadyToPlay] = React.useState(false)
    const [remainingTimeMs, setRemainingTimeMs] = React.useState(null)

    const loading = remainingTimeMs === null

    function clearBoards(gameRules: LayoutDefinitionRules){
        const newBoard = emptyBoard(gameRules.boardSide)
        setVisibleBoard(newBoard)
        boardSnapshot.current = newBoard
        setAvailableShips(gameRules.ships)
        setPlacedShips([])
        setShipSelected(null)
    }

    React.useEffect(() => {
        if(!authServices.isLoggedIn()){
            navigate(AppRoutes.LOGIN, { replace: true }) 
            return
        }

        const getGameState = async (gameID: number) => {
            const response = await api.getGameState(gameID)
            return response.properties
        }

        const getMyBoard = async (gameID: number) => {
            const response = await api.getBoard(gameID, GameTurn.MY)
            return response.properties
        }

        const checkGameState = (gameState: GameState) => {
            if(gameState === GameState.PLAYING){
                navigate(shootingGamePhaseURL, { replace: true })
            }else if(gameState !== GameState.PLACING_SHIPS){
                const modalMessage = gameState === GameState.FINISHED ? ModalMessages.Finished : ModalMessages.Cancelled
                setCustomModalState({ message: modalMessage, isOpen: true })
            }
        }

        const getRequiredRules = async () => {
            const response = await api.getGameRules(validatedGameID)
            const gameRulesDTO: IGameRulesDTO = response.properties

            const fleetComposition: Map<string, number> = gameRulesDTO.shipRules.fleetComposition
            const shipSizes = Object
                .entries(fleetComposition)
                .flatMap(([shipSize, shipCount]) => {
                    return Array(shipCount).fill(parseInt(shipSize))
                })
                
            const ships: Ship[] = shipSizes.map((size, index) => {
                return new Ship(index+1, size, Orientation.horizontal);
            })
            
            return   {
                ships: ships, 
                boardSide: gameRulesDTO.boardSide, 
                layoutDefinitionTimeout: gameRulesDTO.layoutDefinitionTimeout 
            }
        }

        const run = async () => {
            const gameStateDTO = await getGameState(validatedGameID)
            const myBoardDTO = await getMyBoard(validatedGameID)

            checkGameState(GameState[gameStateDTO.state])
            const placeShipsRules = await getRequiredRules()
            gameRules.current = placeShipsRules
            if(myBoardDTO.shipParts.length === 0){
                clearBoards(placeShipsRules)
            }else{
                const myDomainBoard = toBoard(myBoardDTO)
                boardSnapshot.current = myDomainBoard
                setVisibleBoard(myDomainBoard)
                setReadyToPlay(true)
            }
            setRemainingTimeMs(gameStateDTO.remainingTime)
        }
        
        run()

    }, [])
    

    React.useEffect(() => { // Starts polling as soon as the player is ready

        if(!readyToPlay) return
        console.log("Started polling for game state.")

        const checkGameState = async () => {
            const gameStateSiren = await api.getGameState(validatedGameID)
            const state = gameStateSiren.properties.state
            const gameState = GameState[state]
            if(gameState === GameState.PLAYING){ 
                navigate(shootingGamePhaseURL)
                clearInterval(intervalID)
                return 
            }
            console.log("Opponent not ready yet.")
        }
        
        const intervalID: NodeJS.Timer = setInterval(() => {
            checkGameState()
            .catch((problem) => {
                if(problem.status === 401){
                    setCustomModalState({ message: ModalMessages.NotLoggedIn, isOpen: true })
                }
                console.log(`Stopped polling for game state.`)
                clearInterval(intervalID)
            })
                
        }, INTERVAL_TIME_MS)
        

        return () => {
            clearInterval(intervalID)
        }
    }, [readyToPlay])

    const onShipClicked = (shipID: number) => {
        const ship = availableShips.find((ship) => ship.id === shipID)
        setShipSelected(ship)
    }
   
    const onSquareClicked = (squareClicked: Square) => {
        if(shipSelected == null) return
        const currentBoard = boardSnapshot.current
        if(!currentBoard.canPlace(shipSelected, squareClicked)) return
        setVisibleBoard(currentBoard.place(shipSelected, squareClicked))
       
        setAvailableShips((previousShips: Ship[]): Ship[] =>
            previousShips.filter((ship) => ship.id !== shipSelected.id)
        )

        setPlacedShips([
            ...placedShips,
            new ShipInfo(squareClicked.toDTO(), shipSelected.size, shipSelected.orientation)
        ])

        setShipSelected(null);
        boardSnapshot.current = visibleBoard
    }

    const onSquareHover = (squareHovered: Square) => {
        if(shipSelected == null) return
        setVisibleBoard(() => {
            const board = boardSnapshot.current
            return board.canPlace(shipSelected, squareHovered) ?
                         board.place(shipSelected, squareHovered) : 
                         board.placeInvalid(shipSelected, squareHovered)
        })
    }

    const onSquareLeave = (squareHovered: Square) => {
        if(shipSelected == null) return
        setVisibleBoard(boardSnapshot.current)
    }

    const onBoardMouseDown = (event: React.MouseEvent, square: Square) => {
        if(shipSelected != null && event.button === RIGHT_MOUSE_CLICK_EVENT){
            const newShip = shipSelected.rotate()
            setShipSelected(newShip)
            setVisibleBoard(() => {
                const board = boardSnapshot.current

                return board.canPlace(newShip, square) ? 
                            board.place(newShip, square) : 
                            board.placeInvalid(newShip, square)
                }
            )
        }
    }

    const onTimeout = () => {
        setCustomModalState({ message: ModalMessages.Cancelled, isOpen: true })
    }

    const onFleetSubmit = () => {
        if(readyToPlay) return

        api.defineShipLayout(validatedGameID, placedShips)
        .then(() => {
            SuccessToast("Ships placed successfully!")
            setReadyToPlay(true)
        })
    }

    const boardControls: BoardControls = {
        onSquareClick: onSquareClicked,
        onSquareHover: onSquareHover,
        onSquareLeave: onSquareLeave,
        onMouseDown: onBoardMouseDown
    }

    const fleetState: FleetState = {
        availableShips: availableShips,
        shipSelected: shipSelected,
    }

    const fleetControls: FleetControls = {
        onShipClick: onShipClicked,
        onResetRequested: () => {
            if(!readyToPlay && placedShips.length !== 0){
                clearBoards(gameRules.current)
                InfoToast("Board cleared")
        }  },
        onSubmitRequested: () => { onFleetSubmit() }
    }

    const handleModalClose = () => navigate(AppRoutes.HOME, { replace: true })

    return !loading ? (
        <div>
            <PlaceShipView
                board={visibleBoard}
                boardControls={boardControls}
                layoutDefinitionTimeout={gameRules.current?.layoutDefinitionTimeout}
                layoutDefinitionRemainingTimeMs={remainingTimeMs}
                fleetState={fleetState}
                fleetControls={fleetControls}
                loading={loading}
                timerResetToggle={null}
                onTimeout={onTimeout}
            />
            <AnimatedModal
                message={customModalState.message}
                show={customModalState.isOpen}
                handleClose={handleModalClose}
            />
        </div>
    ) : <div className='screen-centered'> 
            <CircularProgress size='6rem' />
        </div>
}
