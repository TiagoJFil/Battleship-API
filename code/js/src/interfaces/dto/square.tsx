export interface SquareDTO{
    row: Row,
    column: Column
}

export class Row{
    ordinal: number
    
    constructor(ordinal: number){
        this.ordinal = ordinal
    }
}



export class Column{
    ordinal: number

    constructor(ordinal: number){
        this.ordinal = ordinal
    }
}