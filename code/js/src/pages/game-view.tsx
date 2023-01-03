import * as React from "react"
import '../css/game.css'
import { BoardView } from "../components/board/board-view"
import { ProgressTimer } from "../components/progress-timer"
import { Board } from "../components/entities/board"
import { Square } from "../components/entities/square"
import { CircularProgress } from "@mui/material"

interface GameViewProps{
    loading: boolean
    playerBoard: Board
    opponentBoard: Board
    shotsDefinitionTimeout: number
    timerResetToggle: boolean
    onOpponentBoardSquareClick: (square: Square) => void
    onTimerTimeout: () => void
}

export function GameView(
    {
        loading,
        playerBoard,
        opponentBoard,
        onOpponentBoardSquareClick: onBoardSquareClick,
        shotsDefinitionTimeout,
        timerResetToggle,
        onTimerTimeout
    }: GameViewProps
){
    return !loading ? (
        <section id="game-view">
            <div className="game-view-space">
                <div className="boards-space">
                    <BoardView 
                        board={playerBoard}
                        controls={{}} // No controls for the player board
                    />
                </div>
                <div className="timer-space">
                    <ProgressTimer
                        timeout={shotsDefinitionTimeout}
                        onTimeout={onTimerTimeout}
                        resetToggle={timerResetToggle}
                    /> 
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={opponentBoard}
                        controls={{onSquareClick: onBoardSquareClick}} // Controls for the opponent board
                    />
                </div>
            </div>  
    </section>
    ) : <CircularProgress />
}