import * as React from "react"
import { PlayerBoard } from "../board/PlayerBoard"
import { Fleet } from "../fleet/Fleet"
import { Ship } from "../utils/Ship"
import { SquareType } from "./Game"
import '../../css/game.css'

interface Props{
    availableShips: Ship[],
    selectShip: (shipSize: number) => void,
    currentlyPlacing: Ship,
    setCurrentlyPlacing: (value: Ship) => void,
    placeShip: (ship: Ship) => void,
    placedShips: Ship[],
    rotateShip,
    resetPlacement: () => void,
    tiles: SquareType[]
}

export function GameView(
    props: Props
){
    return(
        <section id="game-view">
            <div className="game-view-space">
                <div className="fleet-space">
                    <Fleet
                        availableShips={props.availableShips}
                        selectShip={props.selectShip}
                        currentlyPlacing={props.currentlyPlacing}
                        resetPlacement={props.resetPlacement}
                    />
                </div>
                <div className="boards-space">
                    <PlayerBoard
                        tiles={props.tiles}
                        setCurrentlyPlacing={props.setCurrentlyPlacing}
                        placeShip={props.placeShip}
                        placedShips={props.placedShips}
                        currentlyPlacing={props.currentlyPlacing}
                        rotateShip={props.rotateShip}
                    />
                </div>
                <div className="timer-space">

                </div>
            </div>  
    </section>
    )
}