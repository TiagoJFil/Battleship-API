import * as React from "react"
import '../css/game.css'
import { BoardView } from "../components/board/board-view"
import { TimeoutBar } from "../components/progress-bar"
import { Board } from "../components/entities/board"
import { Square } from "../components/entities/square"

interface GameViewProps{
    playerBoard: Board
    opponentBoard: Board
    onBoardSquareClick: (square: Square) => void
    timeoutBarPercentage: number
}

export function GameView(
    {
       playerBoard,
       opponentBoard,
       onBoardSquareClick,
       timeoutBarPercentage,
    }: GameViewProps
){
    return(
        <section id="game-view">
            <div className="game-view-space">
                <div className="boards-space">
                    <BoardView 
                        board={playerBoard}
                        onSquareClick={() => {}}
                        onSquareHover={() => {}}
                        onSquareLeave={() => {}}
                        onMouseDown={() => {}}
                    />
                </div>
                <div className="timer-space">
                    <TimeoutBar
                        barPercentage={timeoutBarPercentage}
                    /> 
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={opponentBoard}
                        onSquareClick={onBoardSquareClick}
                        onSquareHover={() => {}}
                        onSquareLeave={() => {}}
                        onMouseDown={() => {}}
                    />
                </div>
            </div>  
    </section>
    )
}