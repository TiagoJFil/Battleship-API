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