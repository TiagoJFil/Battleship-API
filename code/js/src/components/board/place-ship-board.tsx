import * as React from 'react';
import '../../css/board.css';
import { SquareDTO } from '../../interfaces/entities/square';
import { SquareType } from '../place-ships/place-ships';
import { Ship } from '../utils/ship';

export class Board{
    side: number
    shipParts: Square[]
    shots: Square[]
    hits: Square[]

    constructor(side: number, shipParts: Square[], shots: Square[], hits: Square[]){
        this.side = side
        this.shipParts = shipParts
        this.shots = shots
        this.hits = hits
    }

    asMap(): Map<string, SquareType>{
        const map = new Map<string, SquareType>()
        this.shipParts.forEach((square) => map.set(square.toID(), SquareType.ship_part))
        this.shots.forEach((square) => map.set(square.toID(), SquareType.shot))
        this.hits.forEach((square) => map.set(square.toID(), SquareType.hit))
        return map
    }   

    place(initialSquare: Square, ship: Ship){
        return new Board(
            this.side,
            this.shipParts.concat(ship.getSquares(initialSquare)),
            this.shots,
            this.hits
        )
    }
}

export function emptyBoard(side: number): Board{
    return new Board(side, [], [], [])
}

interface BoardViewProp{
    onSquareHover: (square: Square) => void
    onSquareClick: (square: Square) => void
    board: Board
}

export class Square{
    row: number
    column: number

    constructor(x: number, y: number){
        this.row = x
        this.column = y
    }

    toID(): string{
        return `${this.row}-${this.column}`
    }
}

export function BoardView(
    {board, onSquareHover, onSquareClick}: BoardViewProp
){
    const boardRepresentation = board.asMap()
    const squaresViews: React.ReactElement[] = []

    for(let row = 0; row < board.side; row++){
        for(let col = 0; col < board.side; col++){
            const square = new Square(row, col)
            const type = boardRepresentation.get(square.toID()) ?? SquareType.water
            squaresViews.push(
                <div
                    className={`square ${type}`} 
                    key={`${row}-${col}`}
                    onClick={() => onSquareClick(square)}
                    onMouseOver={() => onSquareHover(square)}
                />
            )
        }
    }

    const disableContextMenu = (e: {preventDefault: () => void}) => {
        e.preventDefault();

    }

    return (
        <div className="board" onContextMenu= {disableContextMenu}>
            {squaresViews}
        </div>
    )
}



