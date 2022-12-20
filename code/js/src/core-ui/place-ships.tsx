import * as React from 'react'
import { useNavigate, useParams } from "react-router-dom";
import { emptyBoard, Board } from '../components/entities/board';
import { Orientation } from '../components/entities/orientation';
import { Ship } from '../components/entities/ship';
import { Square } from '../components/entities/square';
import { PlaceShipView } from '../pages/place-ships-view';
import { GameState } from '../components/entities/game-state';
import { defineShipLayout, getBoard, getGameRules, getGameState } from '../api/api';
import { ShipInfo } from '../components/entities/ship-info';
import { authServices } from '../api/auth';
 
const RIGHT_MOUSE_CLICK_EVENT = 2

export function PlaceShips(){
    const navigate = useNavigate();
    let { gameID } = useParams()
    const validatedGameID = parseInt(gameID)
    
    const shootingGamePhaseURL = `/game/${gameID}`
    const intervalTimeMs = 1000
    
    const [loading, setLoading] = React.useState(true)
    
    const boardSnapshot = React.useRef<Board>(null)
    const [visibleBoard, setVisibleBoard] = React.useState(null)
    const [shipSelected, setShipSelected] = React.useState(null)
    const [availableShips, setAvailableShips] = React.useState([])
    const initialShips = React.useRef(null)
    const initialBoardSide = React.useRef(null)
    const [state, setState] = React.useState(GameState.PLACING_SHIPS)
    const [placedShips, setPlacedShips] = React.useState([])
    const [layoutDefinitionTimeout, setLayoutDefinitionTimeout] = React.useState(null)
    
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

            setAvailableShips(ships)
            setVisibleBoard(emptyBoard(gameRulesDTO.boardSide))
            boardSnapshot.current = emptyBoard(gameRulesDTO.boardSide)
            setLayoutDefinitionTimeout(gameRulesDTO.layoutDefinitionTimeout)
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
        console.log("placed ships: ", placedShips)
        await defineShipLayout(validatedGameID, placedShips)
        //TODO: handle error
    }

    const timeoutSeconds = layoutDefinitionTimeout / 1000

    const [percentage, setPercentage] = React.useState(100)
    const [remainingTime, setRemainingTime] = React.useState(null)


    React.useEffect(() => {
        if(loading) return
        setRemainingTime(timeoutSeconds)
        const intervalID = setInterval(() => {
            setRemainingTime((prev) => {
                if(prev == 0){
                    const stateValue = GameState[state]
                    if(stateValue === GameState.PLACING_SHIPS){   
                        //TIMEOUT 
                        navigate('/')
                    }         
                    clearInterval(intervalID)
                    return
                }
                return prev - 1
            })
            
            setPercentage((previousPercentage) => {
                return previousPercentage > 0 ? previousPercentage - 100 / timeoutSeconds : 0
            })
        }, intervalTimeMs)
    }, [loading])

    React.useEffect(() => {
        if(loading) return
        const checkGameState = async () => {
            const gameStateSiren = await getGameState(validatedGameID)
            const stateKey = gameStateSiren.properties.state
            setState(stateKey)
            const stateValue = GameState[stateKey]
            if(stateValue === GameState.PLAYING){ 
                navigate(shootingGamePhaseURL)
                clearInterval(intervalID);
            }
        }
        
        const intervalID = setInterval(() => {
             checkGameState()  
        }, intervalTimeMs)
    }, [loading])

    return(
        <div>
            <PlaceShipView
                board={visibleBoard}
                availableShips={availableShips}
                onBoardSquareClick={onSquareClicked}
                onBoardSquareHover={onSquareHover}
                onBoardSquareLeave={onSquareLeave}
                onShipClick={onShipClicked}
                shipSelected={shipSelected}
                onFleetResetRequested={resetPlacement}
                onFleetSubmitRequested={submitPlacement}
                onBoardMouseDown={onBoardMouseDown}
                timeoutBarPercentage={percentage}
                loading={loading}
            />
        </div>
    )
}

