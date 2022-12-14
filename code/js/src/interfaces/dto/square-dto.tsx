import { Square } from "../../components/entities/square"

export interface ISquareDTO{
    row: IRow,
    column: IColumn
}

export class IRow{
    ordinal: number
    
    constructor(ordinal: number){
        this.ordinal = ordinal
    }
}

export class IColumn{
    ordinal: number

    constructor(ordinal: number){
        this.ordinal = ordinal
    }
}

class SquareDTO implements ISquareDTO{
    row: IRow
    column: IColumn

    constructor(row: IRow, column: IColumn){
        this.row = row
        this.column = column
    }
}

export function toSquare(squareDTO: SquareDTO): Square {
    return new Square(squareDTO.row.ordinal, squareDTO.column.ordinal)
}

export function toSquares(squareDTOs: SquareDTO[]): Square[] {
    return squareDTOs.map((squareDTO) => toSquare(squareDTO))
}

export function toSquaresDTOFromObject(
    array: {
        row: {ordinal: number}, 
        column: {ordinal: number}
    }[]
): SquareDTO[] {
    return array.map((square) => new SquareDTO(new IRow(square.row.ordinal), new IColumn(square.column.ordinal)))
}