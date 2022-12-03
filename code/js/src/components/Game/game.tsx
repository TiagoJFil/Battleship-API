import * as React from 'react'
import { useParams } from "react-router-dom";


export function Game(){
    let { gameID } = useParams();

    return (
        <div>
            <h1>Game</h1>
            <p id="gameID">{gameID}</p>
        </div>
    )

}