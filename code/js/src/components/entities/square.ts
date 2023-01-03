import { ISquareDTO, IRow, IColumn } from "../../interfaces/dto/square-dto"

export class Square{
    row: number
    column: number

    constructor(row: number, column: number){
        this.row = row
        this.column = column
    }

    toID(): string{
        return `${this.row}-${this.column}`
    }

    toDTO(): ISquareDTO{
        return {
            row: new IRow(this.row),
            column: new IColumn(this.column)
        }
    }
}

export function toSquaresFromObject(array: {row: number, column: number}[]): Square[] {
    return array.map((square) => new Square(square.row, square.column))
}