import * as React from "react"
import '../css/game.css'
import { Board } from "../components/entities/board"
import { BoardView } from "../components/board/board-view"
import { Fleet } from "../components/fleet/fleet-view"
import { Ship } from "../components/entities/ship"
import { Square } from "../components/entities/square"
import { TimeoutBar } from "../components/progress-bar"

interface PlaceShipViewProps{
    board: Board
    availableShips: Ship[]
    shipSelected: Ship
    timeoutBarPercentage: number
    onBoardSquareClick: (square: Square) => void
    onBoardSquareHover: (square: Square) => void
    onBoardSquareLeave: (square: Square) => void
    onShipClick: (shipID: number) => void
    onFleetResetRequested: () => void
    onFleetSubmitRequested: () => void
    onBoardMouseDown: (event: React.MouseEvent, square: Square) => void
}

export function PlaceShipView(
    {
        board, 
        onBoardSquareClick,
        onBoardSquareHover, 
        onBoardSquareLeave,
        availableShips,
        onShipClick,
        shipSelected,
        onFleetResetRequested,
        onFleetSubmitRequested,
        onBoardMouseDown,
        timeoutBarPercentage,
    }: PlaceShipViewProps
){
    return(
        <section id="layout-definition-view">
            <div className="layout-definition-view-space">
                <div className="fleet-space">
                    <Fleet
                        shipSelected={shipSelected}
                        availableShips={availableShips}
                        onClick={onShipClick}
                        onResetRequested={onFleetResetRequested}
                        onSubmitRequested={onFleetSubmitRequested}
                    />
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={board}
                        onSquareClick={onBoardSquareClick}
                        onSquareHover={onBoardSquareHover}
                        onSquareLeave={onBoardSquareLeave}
                        onMouseDown={onBoardMouseDown}
                    />
                </div>
                <div className="timer-space">
                    <TimeoutBar
                        barPercentage={timeoutBarPercentage}
                    /> 
                </div>
            </div>  
    </section>
    )
}