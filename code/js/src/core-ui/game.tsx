import * as React from 'react'
import { useLocation, useParams } from 'react-router-dom'
import { Board, BoardDTO } from '../components/entities/board'
import { Square } from '../components/entities/square'
import { Fleet } from '../components/entities/fleet'
import { GameView } from '../pages/game-view'
import { EmbeddedEntity } from '../interfaces/hypermedia/siren'
import '../css/board.css'
import { defineShot, getBoard } from '../api/api'
import { SquareDTO } from '../interfaces/dto/square'
import { getDiagonals } from '../components/board/utils'
import { SquareType } from '../components/entities/square-type'


export function Game() {
    let { gameID } = useParams()
    const ID = parseInt(gameID)

    
    const playerLayout = useLocation().state.playerBoard
    const opponentLayout = useLocation().state.opponentBoard

    //Boards from the layout definition phase
    //TODO() change this, try to send in the navigate's state a custom class with the boards instead of the objects
    //{'state': {'board': {}, 'board': {}}} -> State(BOARD, BOARD)
    const playerInitialBoard = new Board(
        playerLayout.side,
        playerLayout.shipParts.map((shipPart: Square) => {return new Square(shipPart.row, shipPart.column)}),
        playerLayout.shots,
        playerLayout.hits,
        []
    )

    const opponentInitialBoard = new Board(
        opponentLayout.boardSide,
        opponentLayout.shipParts,
        opponentLayout.shots,
        opponentLayout.hits,
        []
    )

    const [playerBoard, setPlayerBoard] = React.useState(playerInitialBoard)
    const [opponentBoard, setOpponentBoard] = React.useState(opponentInitialBoard)
    const [turn, setTurn] = React.useState(Fleet.MY)
   
    const changeTurn = () => {
        setTurn((prevTurn) =>
          prevTurn === Fleet.MY ? Fleet.OPPONENT : Fleet.MY
        );
    };

    const onSquareClicked = (squareClicked: Square) => {
        const boardRepresentation = opponentBoard.asMap()
        const type = boardRepresentation.get(squareClicked.toID()) ?? SquareType.WATER

        if(turn !== Fleet.MY) return
        if(type !== SquareType.WATER) return
        
        defineShot(ID, [squareClicked.toDTO()]).then((siren) => {
            const entity = siren.entities[0] as EmbeddedEntity<BoardDTO>
            const embeddedProperties = entity.properties
            
            //TODO() make a function that takes a squareDTO and returns a square
            const newOpponentBoard = new Board(
                opponentBoard.side,
                embeddedProperties.shipParts.map((shipPart: SquareDTO) => {return new Square(shipPart.row.ordinal, shipPart.column.ordinal)}),
                embeddedProperties.shots.map((shipPart: SquareDTO) => {return new Square(shipPart.row.ordinal, shipPart.column.ordinal)}),
                embeddedProperties.hits.map((shipPart: SquareDTO) => {return new Square(shipPart.row.ordinal, shipPart.column.ordinal)}),
                []
            )
    
            setOpponentBoard(newOpponentBoard) 
            changeTurn()
        })
    }

    React.useEffect(() => {
        const intervalID = setInterval(() => {
            if(turn === Fleet.OPPONENT){
                getBoard(ID, Fleet.MY).then((siren) => {
                   const boardDTO = siren.properties
                   const shots = boardDTO.shots.map((shot: SquareDTO) => {return new Square(shot.row.ordinal, shot.column.ordinal)})
                   const hits = boardDTO.hits.map((hit: SquareDTO) => {return new Square(hit.row.ordinal, hit.column.ordinal)})
                    
                   if(shots.length !== playerBoard.shots.length || hits.length !== playerBoard.hits.length){
                        const shipParts = boardDTO.shipParts.map((shipPart: SquareDTO) => {return new Square(shipPart.row.ordinal, shipPart.column.ordinal)})
                        const newPlayerBoard = new Board(
                            playerBoard.side,
                            shipParts,
                            shots,
                            hits,
                            []
                        )
                        setPlayerBoard(newPlayerBoard)
                        changeTurn()
                        clearInterval(intervalID)
                   }
                })
            }
        }, 1000)
    }, [opponentBoard])

    return (
        <div>
            <GameView
                playerBoard={playerBoard}
                opponentBoard={opponentBoard}
                onBoardSquareClick={onSquareClicked}
                timeoutBarPercentage={100}
            />
        </div>
    )
}

