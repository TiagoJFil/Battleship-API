import { Board } from "../../components/entities/board"
import { ISquareDTO, toSquares } from "./square-dto"


export interface IBoardDTO{
    side: number
    shipParts: ISquareDTO[]
    shots: ISquareDTO[]
    hits: ISquareDTO[]
}

export function toBoard(dto: IBoardDTO): Board{
    return new Board(
        dto.side,
        toSquares(dto.shipParts),
        toSquares(dto.shots),
        toSquares(dto.hits)
    )
}
