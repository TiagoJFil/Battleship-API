import * as React from "react"
import { MiniShip } from "./mini-ship-view"
import '../../css/mini-fleet.css'
import { Ship } from "../entities/ship"
import { styles } from "../../constants/styles"
import { IconButton } from "../icons"


interface Props{
    availableShips: Ship[],
    shipSelected: Ship,
    onClick: (shipID: number) => void,
    onResetRequested: () => void
    onSubmitRequested: () => void
}

export function Fleet(
    {
        availableShips,
        shipSelected,
        onClick,
        onResetRequested,
        onSubmitRequested
    }: Props
){
    const shipsToPlace = availableShips.map((ship) => {
        return <MiniShip
            key={ship.id}
            ship={ship}
            onShipClicked={onClick}
            currentlyPlacing={shipSelected && shipSelected.id === ship.id}
        />
    })

    const fleet = (
        <div id={styles.FLEET}>
            {shipsToPlace}
        </div>
    )

    return (
        <div className={styles.FLEET_CONTAINER}>
            <div id={styles.REMAINING_SHIPS}>
                {availableShips.length > 0 ? 
                    fleet : 
                    <IconButton iconClass={styles.PLAY_ICON} onClick={onSubmitRequested}></IconButton>
                }
            </div>
            <div className={styles.REDO_CONTAINTER}>
                <div className= {styles.REDO_ICON_BUTTON}>
                    <IconButton iconClass={styles.REDO} onClick={onResetRequested}></IconButton>
                </div>
            </div>
        </div>
    )
}