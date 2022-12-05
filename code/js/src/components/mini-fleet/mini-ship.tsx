import * as React from 'react'
import '../../css/mini-fleet.css'
import { Ship } from '../utils/ship'

interface Props{
    shipSize: number,
    selectShip: (shipSize: number) => void,
    availableShips: Ship[],
    currentlyPlacing: boolean
}

export function MiniShip(
    props: Props
){
    const shipLength = new Array(props.shipSize).fill(null)
    const shipSquares = shipLength.map((_, index) => {
        return <div className="ship-square" key={index}/>
    })

    return(
        <div
            id={"mini-ship"}
            onClick={() => props.selectShip(props.shipSize)}
            className={props.currentlyPlacing ? 'mini-ship placing': 'mini-ship'}
        >
            <div className="mini-ship-squares">{shipSquares}</div>
        </div>
        
    )
}