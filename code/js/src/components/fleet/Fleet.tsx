import * as React from "react"
import { MiniShip } from "../mini-fleet/mini-ship"
import '../../css/mini-fleet.css'
import { Ship } from "../utils/ship"


interface Props{
    availableShips: Ship[],
    shipSelected: Ship,
    onClick: (shipID: number) => void,
    onResetRequested: () => void
}

export function Fleet(
    props: Props
){
    const shipsToPlace = props.availableShips.map((ship) => {
        return <MiniShip
            key={ship.id}
            ship={ship}
            onShipClicked={props.onClick}
            currentlyPlacing={props.shipSelected && props.shipSelected.id === ship.id}
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
                <button onClick={props.onResetRequested}></button>
            </div>
        </div>
    )
}