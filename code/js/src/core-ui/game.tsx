import * as React from 'react'
import { useLocation } from 'react-router-dom'
import { Board, emptyBoard } from '../components/entities/board'
import { Square } from '../components/entities/square'
import { GameRulesDTO } from '../interfaces/dto/game-rules'
import { GameView } from '../pages/game-view'

export function Game() {
    //Change with api call to get player and opponent boards
    const playerLayout = useLocation().state.playerBoard
    const playerInitialBoard = new Board(
        playerLayout.side,
        playerLayout.shipParts.map((shipPart) => {return new Square(shipPart.row, shipPart.column)}),
        playerLayout.shots,
        playerLayout.hits,
        playerLayout.invalidSquares
    )
    const gameRules: GameRulesDTO = useLocation().state.gameRules
    const [playerBoard, setPlayerBoard] = React.useState(playerInitialBoard)
    const [opponentBoard, setOpponentBoard] = React.useState(emptyBoard(gameRules.boardSide))

    return (
        <div>
            <GameView
                playerBoard={playerBoard}
                opponentBoard={opponentBoard}
                timeoutBarPercentage={100}
            />
        </div>
    )
}