import * as React from "react"
import '../css/game.css'
import { BoardView } from "../components/board/board-view"
import { Board } from "../components/entities/board"
import { Square } from "../components/entities/square"
import { Button, CircularProgress } from "@mui/material"
import { GameTurn } from "../components/entities/turn"
import { BarColor, CustomProgressBar } from "../components/progress-bar"

interface GameViewProps{
    loading: boolean
    playerBoard: Board
    opponentBoard: Board
    selectedShots: Square[]
    shotsRemaining: number
    timerPercentage: number
    turn: GameTurn
    onOpponentBoardSquareClick: (square: Square) => void
    onSubmitShotsClick: (button) => void
}

export function GameView(
    {
        loading,
        playerBoard,
        opponentBoard,
        selectedShots,
        onOpponentBoardSquareClick,
        shotsRemaining,
        turn,
        timerPercentage,
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
                    <CustomProgressBar progress={timerPercentage} color={turn === GameTurn.MY ? BarColor.PRIMARY : BarColor.SECONDARY} />
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
                  <Button variant="contained" color="primary" onClick={(e) => onSubmitShotsClick(e.target)}>Submit</Button>
                </div>
            </div>  
    </section>
    ) : <div className='screen-centered'> 
            <CircularProgress size='6rem' />
        </div>
}