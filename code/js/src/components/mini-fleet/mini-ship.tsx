import * as React from 'react'
import '../../css/mini-fleet.css'
import { Ship } from '../utils/ship'

interface Props{
    ship: Ship
    onShipClicked: (shipID: number) => void,
    currentlyPlacing: boolean
}

export function MiniShip(
    props: Props
){
    const shipLength = new Array(props.ship.size).fill(null)
    const shipSquares = shipLength.map((_, index) => {
        return <div className="ship-square" key={index}/>
    })
    
    return(
        <div
            className={props.currentlyPlacing ? 'mini-ship placing': 'mini-ship'}
            id={"mini-ship"}
            key={props.ship.id}
            onClick={() => {props.onShipClicked(props.ship.id)}}  
        >
            <div className="mini-ship-squares">{shipSquares}</div>
        </div>
        
    )
}