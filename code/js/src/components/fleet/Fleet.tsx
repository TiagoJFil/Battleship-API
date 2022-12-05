import * as React from "react"
import { MiniShip } from "../mini-fleet/mini-ship"
import '../../css/mini-fleet.css'
import { Ship } from "../utils/ship"


interface Props{
    availableShips: Ship[],
    selectShip: (shipSize: number) => void,
    currentlyPlacing: Ship,
    resetPlacement: () => void
}

export function Fleet(
    props: Props
){
    const remainingShips = props.availableShips.map((ship) => ship.size)

    const shipsToPlace = remainingShips.map((shipSize) => {
        return <MiniShip
            shipSize={shipSize}
            availableShips={props.availableShips}
            selectShip={props.selectShip}
            currentlyPlacing={props.currentlyPlacing && props.currentlyPlacing.size === shipSize}
        />
    })

    const fleet = (
        <div id="fleet">
            {shipsToPlace}
        </div>
    )

    return (
        <div className="fleet-container">
            <div id="remaining-ships">
                {props.availableShips.length > 0 ? fleet : "Ready to play!"}
            </div>
            <div>
                <button onClick={props.resetPlacement}></button>
            </div>
        </div>
    )
}