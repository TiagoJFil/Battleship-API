import * as React from 'react'
import { useLocation, useParams } from "react-router-dom";
import { GameRules } from '../../interfaces/entities/game-rules';
import { Board, emptyBoard, Square } from '../board/place-ship-board';
import { Orientation } from '../utils/orientation';
import { Ship } from '../utils/ship';
import { PlaceShipView } from './place-ships-view';


const RIGHT_MOUSE_CLICK_EVENT = 2

export enum SquareType{
    water = 'water',
    ship_part = "ship-part",
    shot = "shot",
    hit = "hit",
    forbidden = "forbidden"
}

export function PlaceShips(){
    let { gameID } = useParams()
    const gameRules: GameRules = useLocation().state.properties;
    const fleetComposition: Map<string, number> = gameRules.shipRules.fleetComposition;

    const fleetCompositionKeys = Object.keys(fleetComposition).map(
        (key) => parseInt(key)
    )

    const ships = fleetCompositionKeys.map((size, index) => {
        return new Ship(index+1, size, Orientation.horizontal);
    })

    const [board, setBoard] = React.useState(emptyBoard(gameRules.boardSide))
    const [shipSelected, setShipSelected] = React.useState(null);
    const [availableShips, setAvailableShips] = React.useState(ships);

    
        
    const onShipClicked = (shipID: number) => {
        const ship = availableShips.find((ship) => ship.id === shipID)
        setShipSelected(ship)
    }

    const onSquareClicked = (squareClicked: Square) => {
        if(shipSelected == null) return
        const newBoard = board.place(squareClicked, shipSelected)
        setBoard(newBoard)

        setAvailableShips((previousShips: Ship[]): Ship[] =>
            previousShips.filter((ship) => ship.id !== shipSelected.id)
        )
        setShipSelected(null);
    }

    const onSquareHover = (squareHovered: Square) => {
        if(shipSelected == null) return
        const placedShips = ships.filter((ship) => {
            return ship.id !== shipSelected.id && availableShips.find((availableShip) => availableShip.id === ship.id) == null
        })
        console.log(placedShips)

        const prevBoard = placedShips.reduce((prevBoard, ship) => {
            if(ship.id === shipSelected.id) return prevBoard
            console.log(prevBoard)
            
            return prevBoard.place(squareHovered, ship)
        }, emptyBoard(gameRules.boardSide))
        const newBoard = prevBoard.place(squareHovered, shipSelected)
        setBoard(newBoard)
    }

    const resetPlacement = () => {
        setBoard(emptyBoard(gameRules.boardSide))
        setShipSelected(null);
        setAvailableShips(ships);
    }

    return(
        <div>
            <PlaceShipView
                board={board}
                availableShips={availableShips}
                onBoardSquareClick={onSquareClicked}
                onBoardSquareHover={onSquareHover}
                onShipClick={onShipClicked}
                shipSelected={shipSelected}
                onFleetResetRequested={resetPlacement}
            />
        </div>
    )
}

