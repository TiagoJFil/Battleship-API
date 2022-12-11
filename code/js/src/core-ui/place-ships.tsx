import * as React from 'react'
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { GameRulesDTO } from '../interfaces/dto/game-rules';
import { emptyBoard, Board } from '../components/entities/board';
import { Orientation } from '../components/entities/orientation';
import { Ship } from '../components/entities/ship';
import { Square } from '../components/entities/square';
import { PlaceShipView } from '../pages/place-ships-view';
import { GameState } from '../components/entities/game-state';
import { defineShipLayout, getBoard, getGameState } from '../api/api';
import { ShipInfo } from '../components/entities/ship-info';
import { Fleet } from '../components/entities/fleet';
 
const RIGHT_MOUSE_CLICK_EVENT = 2

export function PlaceShips(){
    const navigate = useNavigate();
    let { gameID } = useParams()
    const gameRules: GameRulesDTO = useLocation().state;
    const fleetComposition: Map<string, number> = gameRules.shipRules.fleetComposition;
    const fleetCompositionKeys = Object.keys(fleetComposition).map(
        (key) => parseInt(key)
    )

    const ships = fleetCompositionKeys.map((size, index) => {
        return new Ship(index+1, size, Orientation.horizontal);
    })

    const initialBoard = emptyBoard(gameRules.boardSide)
    const boardSnapshot = React.useRef<Board>(initialBoard)
    const [visibleBoard, setVisibleBoard] = React.useState(initialBoard)
    const [shipSelected, setShipSelected] = React.useState(null)
    const [availableShips, setAvailableShips] = React.useState(ships)
    const [gameState, setGameState] = React.useState(GameState.PLACING_SHIPS)
    const [placedShips, setPlacedShips] = React.useState([])
    
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
        boardSnapshot.current = emptyBoard(gameRules.boardSide)
        setVisibleBoard(boardSnapshot.current)
        setShipSelected(null);
        setAvailableShips(ships);
    }

    const submitPlacement = () => {
        if(availableShips.length > 0) return
        defineShipLayout(parseInt(gameID), placedShips).then()
        .catch((error) => {
            console.log(error)
        })
    }

    const timeoutSeconds = gameRules.layoutDefinitionTimeout / 1000

    const [percentage, setPercentage] = React.useState(100)
    const [timeout, setTimeout] = React.useState(timeoutSeconds)
    const [intervalId, setIntervalId] = React.useState(null);

    React.useEffect(() => {
        const intervalID = setInterval(() => {  
            setTimeout((prev) => {
                if(prev == 0){
                    const stateValue = GameState[gameState]
                    if(stateValue === GameState.PLACING_SHIPS){   
                        //TIMEOUT 
                        navigate('/')
                    }         
                    clearInterval(intervalID)
                    return
                }
                return prev - 1
            })
            
            setPercentage((prev) => {
                return prev > 0 ? prev - 100/timeoutSeconds : 0
            })

            getGameState(parseInt(gameID)).then((gameInfo) => {
                const currentGameState = gameInfo.properties.state
                const stateValue = GameState[currentGameState]
                setGameState(currentGameState)
                if(stateValue === GameState.PLAYING){
                    
                    getBoard(parseInt(gameID), Fleet.OPPONENT).then((siren) =>{
                        navigate(`/game/${gameID}`, {state: {'playerBoard': boardSnapshot.current, 'opponentBoard': siren.properties}})
                        clearInterval(intervalID);
                    })         
                }
            })  

            setIntervalId(intervalID);
        }, 1000)
    }, [gameState])

    React.useEffect(() => {
        return () => {
          clearInterval(intervalId);
        }
      }, [intervalId]);

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
            />
        </div>
    )
}

