import * as React from 'react';
import '../../css/board.css';
import { Square } from '../entities/square';
import { SquareType } from '../entities/square-type';
import { Board } from '../entities/board';
import { Styles } from '../../constants/styles';


interface BoardViewProps{
    board: Board,
    currentShots: Square[],
    controls: BoardControls
}

export interface BoardControls{
    onSquareHover?: (square: Square) => void
    onSquareClick?: (square: Square) => void
    onSquareLeave?: (square: Square) => void
    onMouseDown?: (event: React.MouseEvent, onSquareMouseDown: Square) => void
}

export function BoardView(
    {
        board, 
        currentShots,
        controls,
    }: BoardViewProps
){
    const boardRepresentation = board.asMap()
    const squaresViews: React.ReactElement[] = []

    const {onSquareHover, onSquareClick, onSquareLeave, onMouseDown} = controls
    const shotsIds = currentShots.map(s => s.toID())
    
    for(let row = 0; row < board.side; row++){
        for(let col = 0; col < board.side; col++){
            const square = new Square(row, col)
            const type = boardRepresentation.get(square.toID()) ?? SquareType.WATER
            const className = shotsIds.includes(square.toID()) ? SquareType.SELECTED : type
            
            squaresViews.push(
                <div 
                    className={className} 
                    key={`${row}-${col}`}
                    onClick={() => {onSquareClick?.(square)}}
                    onMouseEnter={() => onSquareHover?.(square)}
                    onMouseLeave={() => {onSquareLeave?.(square)}}
                    onMouseDown={(event) => onMouseDown?.(event, square)}
                />
            )

        }
    }

    const disableContextMenu = (e: {preventDefault: () => void}) => {
        e.preventDefault();

    }

    return (
        <div className={Styles.BOARD} onContextMenu= {disableContextMenu}>
            {squaresViews}
        </div>
    )
}



