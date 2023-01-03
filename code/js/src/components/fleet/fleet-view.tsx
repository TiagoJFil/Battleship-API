import * as React from "react"
import { MiniShip } from "./mini-ship-view"
import '../../css/mini-fleet.css'
import { Ship } from "../entities/ship"
import { Styles } from "../../constants/styles"
import { IconButton } from "../icons"
import RedoIcon from '@mui/icons-material/Redo';
import CheckIcon from '@mui/icons-material/Check';
interface FleetProps{
    state: FleetState
    controls: FleetControls
}

export interface FleetState{
    availableShips: Ship[]
    shipSelected: Ship
}

export interface FleetControls{
    onShipClick: (shipID: number) => void
    onResetRequested: () => void
    onSubmitRequested: () => void
}

export function Fleet(
    { state, controls }: FleetProps
){

    const { availableShips, shipSelected } = state
    const { onShipClick, onResetRequested, onSubmitRequested } = controls

    const shipsToPlace = availableShips.map((ship) => {
        return <MiniShip
            key={ship.id}
            ship={ship}
            onShipClicked={onShipClick}
            currentlyPlacing={shipSelected && shipSelected.id === ship.id}
        />
    })

    const fleet = (
        <div id={Styles.FLEET}>
            {shipsToPlace}
        </div>
    )

    return (
        <div className={Styles.FLEET_CONTAINER}>
            <div id={Styles.REMAINING_SHIPS}>
                {availableShips.length > 0 ? 
                    fleet : 
                    <IconButton icon={CheckIcon} onClick={onSubmitRequested}></IconButton>
                }
            </div>
            <div className={Styles.REDO_CONTAINTER}>
                <div className= {Styles.REDO_ICON_BUTTON}>
                    <IconButton icon={RedoIcon} onClick={onResetRequested}></IconButton>
                </div>
            </div>
        </div>
    )
}