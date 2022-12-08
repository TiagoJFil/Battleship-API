import * as React from 'react'
import { useLocation, useParams } from "react-router-dom";
import { GameRulesDTO } from '../interfaces/dto/game-rules';
import { emptyBoard, Board } from '../components/entities/board';
import { Orientation } from '../components/entities/orientation';
import { Ship } from '../components/entities/ship';
import { Square } from '../components/entities/square';
import { PlaceShipView } from '../pages/place-ships-view';
import { GameState } from '../components/entities/game-state';
import { getGameState } from '../api/api';
 
const RIGHT_MOUSE_CLICK_EVENT = 2

export function PlaceShips(){
    let { gameID } = useParams()
    const gameRules: GameRulesDTO = useLocation().state.properties;
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
    
    React.useEffect(() => {

    })
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
        setGameState(GameState.PLAYING)
    }

    const timeoutSeconds = gameRules.layoutDefinitionTimeout / 1000

    const [percentage, setPercentage] = React.useState(100)
    const [timeout, setTimeout] = React.useState(timeoutSeconds)

    //TODO change, renders the ship view every second when only the timeout bar should be updated
    //TODO handle the case when the timeout is 0, when a player has requested to confirm its layout the progress bar stops moving
    //but internally the timeout is still counting down, so when the timeout is 0 both players will check the gameState and see if it is cancelled, 
    //meaning that the player has not confirmed its layout in time
    React.useEffect(() => {
        const intervalID = setInterval(async () => {      
            setTimeout((prev) => {
                if(prev == 0){
                    clearInterval(intervalID);
                }
                return prev - 1
            })
            
            setPercentage((prev) => {
              return prev > 0 ? prev - 100/timeoutSeconds : 0
            })
            
        }, 1000)
    }, [])

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

