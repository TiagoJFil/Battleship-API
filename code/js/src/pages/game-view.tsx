import * as React from "react"
import '../css/game.css'
import { BoardView } from "../components/board/board-view"
import { TimeoutBar } from "../components/progress-bar"
import { Board } from "../components/entities/board"
import { Square } from "../components/entities/square"

interface GameViewProps{
    loading: boolean
    playerBoard: Board
    opponentBoard: Board
    shotsDefinitionTimeout: number
    onBoardSquareClick: (square: Square) => void
}

export function GameView(
    {
       loading,
       playerBoard,
       opponentBoard,
       onBoardSquareClick,
       shotsDefinitionTimeout,
    }: GameViewProps
){
    return !loading ? (
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
                        timeout={shotsDefinitionTimeout}
                        onTimeout={() => {}}
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
    ) : <div> Loading...</div>
}