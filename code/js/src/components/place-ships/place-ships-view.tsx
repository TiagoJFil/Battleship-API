import * as React from "react"
import '../../css/game.css'
import { Board, BoardView, Square } from "../board/place-ship-board"
import { Fleet } from "../fleet/fleet"
import { Ship } from "../utils/ship"

interface PlaceShipViewProps{
    board: Board
    availableShips: Ship[]
    shipSelected: Ship
    onBoardSquareClick: (square: Square) => void
    onBoardSquareHover: (square: Square) => void
    onShipClick: (shipID: number) => void
    onFleetResetRequested: () => void
}

export function PlaceShipView(
    {
        board, 
        onBoardSquareClick,
        onBoardSquareHover, 
        availableShips,
        onShipClick,
        shipSelected,
        onFleetResetRequested
    }: PlaceShipViewProps
){
    return(
        <section id="game-view">
            <div className="game-view-space">
                <div className="fleet-space">
                    <Fleet
                        shipSelected={shipSelected}
                        availableShips={availableShips}
                        onClick={onShipClick}
                        onResetRequested={onFleetResetRequested}
                    />
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={board}
                        onSquareClick={onBoardSquareClick}
                        onSquareHover={onBoardSquareHover}
                    />
                </div>
                <div className="timer-space">

                </div>
            </div>  
    </section>
    )
}