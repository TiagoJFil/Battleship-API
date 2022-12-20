import * as React from "react"

export function GameButton(props : {text : string, onClick : (event : any) => void}){
    return (
        <div className="GameButtonContainer">
            <button className="GameButton" onClick={(event) => {props.onClick(event)}}>{props.text}</button>
        </div>
    )
}