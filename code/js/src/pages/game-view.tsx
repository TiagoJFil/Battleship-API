import * as React from "react"
import '../css/game.css'
import { BoardView } from "../components/board/board-view"
import { ProgressTimer } from "../components/progress-timer"
import { Board } from "../components/entities/board"
import { Square } from "../components/entities/square"
import { Button, CircularProgress } from "@mui/material"
import { GameTurn } from "../components/entities/turn"
import { BarColor } from "../components/progress-bar"

interface GameViewProps{
    loading: boolean
    playerBoard: Board
    opponentBoard: Board
    shotsDefinitionTimeout: number
    selectedShots: Square[]
    shotsRemaining: number
    shotsDefinitionRemainingTimeMs: number
    timerResetToggle: boolean
    turn: GameTurn
    onOpponentBoardSquareClick: (square: Square) => void
    onTimerTimeout: () => void
    onSubmitShotsClick: () => void
}

export function GameView(
    {
        loading,
        playerBoard,
        opponentBoard,
        selectedShots,
        onOpponentBoardSquareClick,
        shotsDefinitionTimeout,
        shotsRemaining,
        shotsDefinitionRemainingTimeMs,
        turn,
        timerResetToggle,
        onTimerTimeout,
        onSubmitShotsClick,
    }: GameViewProps
){
    return !loading ? (
        <section id="game-view">
            <div className="game-view-space">
                <div className="boards-space">
                    <BoardView 
                        board={playerBoard}
                        currentShots={[]}
                        controls={{}} // No controls for the player board
                    />
                </div>
                <div className="timer-space">
                    <ProgressTimer
                        maxValue={shotsDefinitionTimeout}
                        startValue={shotsDefinitionRemainingTimeMs}
                        onTimeout={onTimerTimeout}
                        barColor={turn === GameTurn.MY ? BarColor.PRIMARY : BarColor.SECONDARY}
                        resetToggle={timerResetToggle}
                    /> 
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={opponentBoard}
                        currentShots={selectedShots}
                        controls={{onSquareClick: onOpponentBoardSquareClick}} // Controls for the opponent board
                    />
                </div>
                <div className="shots-remaining">
                    Shots remaining: {shotsRemaining}
                </div>
                <div className="submit-shots">
                    <Button variant="contained" color="primary" onClick={onSubmitShotsClick}>Submit</Button>
                </div>
            </div>  
    </section>
    ) : <div className='screen-centered'> 
            <CircularProgress size='6rem' />
        </div>
}