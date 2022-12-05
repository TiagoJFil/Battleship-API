import * as React from 'react'
import { useParams } from "react-router-dom";
import { Orientation } from '../utils/Orientation';
import { Ship } from '../utils/Ship';
import { GameView } from './GameView';


const RIGHT_MOUSE_CLICK_EVENT = 2

export enum SquareType{
    water = "#",
    ship_part = "B",
    shot = "O",
    hit = "X",
    forbidden = "F",
}

enum GameState{
    placing_ships = "placing_ships",
    playing = "playing",
    finished = "finished",
    cancelled = "cancelled",
}

const fleetComposition = new Map<number, number>([
    [1, 1],
    [2, 1],
    [3, 1],
    [4, 1],
    [5, 1]
])

export function Game(){
    let { gameID } = useParams();

    let tiles = Array<SquareType>(64);
    tiles = tiles.fill(SquareType.water);

    const shipSizes = Array.from(fleetComposition.keys());
    const ships = shipSizes.map((size) => {
        return new Ship(size, Orientation.horizontal, null, false);
    })

    const [currentlyPlacing, setCurrentlyPlacing] = React.useState(null);
    const [placedShips, setPlacedShips] = React.useState([]);
    const [availableShips, setAvailableShips] = React.useState(ships);
    //const[gameState, setGameState] = React.useState(GameState.placing_ships);

    const selectShip = (shipSize: number) => {
        const ship = availableShips.find((ship) => ship.size === shipSize)
        setCurrentlyPlacing(
            new Ship(ship.size, Orientation.horizontal, null, false)
        )
    }

    const placeShip = (currentlyPlacing: Ship) => {
        setPlacedShips([
            ...placedShips,
            new  Ship(currentlyPlacing.size, currentlyPlacing.orientation, currentlyPlacing.position, true)
        ])
        setAvailableShips((previousShips: Ship[]): Ship[] =>
            previousShips.filter((ship) => ship.size !== currentlyPlacing.size && ship.placed === false)
        )
        setCurrentlyPlacing(null);
    }

    const rotateShip = (event: MouseEvent) => {
        if (currentlyPlacing != null && event.button === RIGHT_MOUSE_CLICK_EVENT) {
          setCurrentlyPlacing(
            new Ship(
                currentlyPlacing.size,
                currentlyPlacing.orientation === Orientation.vertical ? Orientation.horizontal : Orientation.vertical,
                currentlyPlacing.position,
                currentlyPlacing.placed
            )
          )
        }
      }

    const resetPlacement = () => {
        setCurrentlyPlacing(null);
        setPlacedShips([]);
        setAvailableShips(ships);
    }

    return(
        <div>
            <GameView
                availableShips={availableShips}
                selectShip={selectShip}
                currentlyPlacing={currentlyPlacing}
                setCurrentlyPlacing={setCurrentlyPlacing}
                placeShip={placeShip}
                placedShips={placedShips}
                rotateShip={rotateShip}
                resetPlacement={resetPlacement}
                tiles={tiles}
            />
        </div>
    )
}
