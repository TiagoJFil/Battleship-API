import * as React from 'react'
import { useNavigate, useParams } from "react-router-dom";
import { emptyBoard, Board } from '../components/entities/board';
import { Orientation } from '../components/entities/orientation';
import { Ship } from '../components/entities/ship';
import { Square } from '../components/entities/square';
import { PlaceShipView } from '../pages/place-ships-view';
import { GameState } from '../components/entities/game-state';
import { defineShipLayout, getGameRules, getGameState } from '../api/api';
import { ShipInfo } from '../components/entities/ship-info';
import { authServices } from '../api/auth';
 
const RIGHT_MOUSE_CLICK_EVENT = 2
const INTERVAL_TIME_MS = 1000

export function PlaceShips(){
    const navigate = useNavigate();
    let { gameID } = useParams()
    const validatedGameID = parseInt(gameID)
    
    const shootingGamePhaseURL = `/game/${gameID}`
    
    const boardSnapshot = React.useRef<Board>(null)
    const initialShips = React.useRef(null)
    const initialBoardSide = React.useRef(null)
    const [loading, setLoading] = React.useState(true)
    const [visibleBoard, setVisibleBoard] = React.useState(null)
    const [shipSelected, setShipSelected] = React.useState(null)
    const [availableShips, setAvailableShips] = React.useState([])
    const [state, setState] = React.useState(GameState.PLACING_SHIPS)
    const [placedShips, setPlacedShips] = React.useState([])
    const [layoutDefinitionTimeout, setLayoutDefinitionTimeout] = React.useState(null)
    const [isTimedOut, setIsTimedOut] = React.useState(false)
    
    React.useEffect(() => {
        if(!authServices.isLoggedIn()){
            navigate('/login', { replace: true }) 
            return
        }
        
        const getRules = async () => {
            const response = await getGameRules(validatedGameID)
            const gameRulesDTO = response.properties

            const fleetComposition: Map<string, number> = gameRulesDTO.shipRules.fleetComposition
 
            const fleetCompositionKeys = Object.keys(fleetComposition).map(
                (key) => parseInt(key)
            )
            const ships = fleetCompositionKeys.map((size, index) => {
                return new Ship(index+1, size, Orientation.horizontal);
            })

            setVisibleBoard(emptyBoard(gameRulesDTO.boardSide))
            boardSnapshot.current = emptyBoard(gameRulesDTO.boardSide)
            
            setLayoutDefinitionTimeout(gameRulesDTO.layoutDefinitionTimeout)
            setAvailableShips(ships)
            initialShips.current = ships
            initialBoardSide.current = gameRulesDTO.boardSide	
            
            setLoading(false)
        } 

        getRules()
    }, [])

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

    const resetPlacement = () => {
        boardSnapshot.current = emptyBoard(initialBoardSide.current)
        setVisibleBoard(boardSnapshot.current)
        setShipSelected(null);
        setPlacedShips([])
        setAvailableShips(initialShips.current);
    }

    const submitPlacement = async () => {
        await defineShipLayout(validatedGameID, placedShips)
        //TODO: handle error
    }

    const onTimeout = () => {
        setIsTimedOut(true)
    }

    React.useEffect(() => {
        if(loading) return

        const checkGameState = async () => {
            const gameStateSiren = await getGameState(validatedGameID)
            const state = gameStateSiren.properties.state
            setState(state)
            const gameState = GameState[state]
            if(gameState === GameState.PLAYING){ 
                navigate(shootingGamePhaseURL)
                clearInterval(intervalID);
            }
        }
        
        const intervalID = setInterval(() => {
             checkGameState()  
        }, INTERVAL_TIME_MS)

        return () => {
            clearInterval(intervalID)
        }
    }, [loading])


    return(
        <div>
            <PlaceShipView
                board={visibleBoard}
                gameState={state}
                layoutDefinitionTimeout={layoutDefinitionTimeout}
                availableShips={availableShips}
                onBoardSquareClick={onSquareClicked}
                onBoardSquareHover={onSquareHover}
                onBoardSquareLeave={onSquareLeave}
                onShipClick={onShipClicked}
                shipSelected={shipSelected}
                onFleetResetRequested={resetPlacement}
                onFleetSubmitRequested={submitPlacement}
                onBoardMouseDown={onBoardMouseDown}
                loading={loading}
                isTimedOut={isTimedOut}
                onTimeout={onTimeout}
            />
        </div>
    )
}

