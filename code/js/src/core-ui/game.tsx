import * as React from 'react'
import { useLocation, useParams } from 'react-router-dom'
import { Square } from '../components/entities/square'
import { Fleet } from '../components/entities/fleet'
import { GameView } from '../pages/game-view'
import { EmbeddedEntity, SirenEntity } from '../interfaces/hypermedia/siren'
import '../css/board.css'
import { defineShot, getBoard } from '../api/api'
import { SquareType } from '../components/entities/square-type'
import { IBoardDTO, toBoard } from '../interfaces/dto/board-dto'
import { Board } from '../components/entities/board'


export function Game() {
    let { gameID } = useParams()
    const validatedGameID = parseInt(gameID)

    const [currentPlayerBoard, setPlayerBoard] = React.useState<Board>(null)
    const [currentOpponentBoard, setOpponentBoard] = React.useState<Board>(null)
    const [turn, setTurn] = React.useState(Fleet.MY) //TODO: FIX THIS, check who is the first to play
    
    React.useEffect(() => {
        const getBoards = async () => {
            const playerBoardResponse = await getBoard(validatedGameID, Fleet.MY)
            const opponentBoardResponse = await getBoard(validatedGameID, Fleet.OPPONENT)

            const playerBoardDTO = playerBoardResponse.properties 
            const opponentBoardDTO = opponentBoardResponse.properties 
            console.log(playerBoardDTO)
            console.log(opponentBoardDTO)
            setPlayerBoard(toBoard(playerBoardDTO))
            setOpponentBoard(toBoard(opponentBoardDTO))
        }

        getBoards()
    }, [])


    const changeTurn = () => {
        setTurn((prevTurn) =>
          prevTurn === Fleet.MY ? Fleet.OPPONENT : Fleet.MY
        );
    };

    const onSquareClicked = (squareClicked: Square) => {
        const boardRepresentation = currentOpponentBoard.asMap()

        const squareID = squareClicked.toID()
        const squareType = boardRepresentation.get(squareID) ?? SquareType.WATER

        if(turn !== Fleet.MY) return
        if(squareType !== SquareType.WATER) return
        
        const makeShot = async () => {
            const squareDTO = squareClicked.toDTO()
            const shots = [squareDTO]
            
            const sirenResponse = await defineShot(validatedGameID, shots)
            const embeddedEntity = sirenResponse.entities[0] as EmbeddedEntity<IBoardDTO>
            const embeddedBoardDTO = embeddedEntity.properties
           

            setOpponentBoard(toBoard(embeddedBoardDTO))
            changeTurn()
        }

        makeShot()
    }

    React.useEffect(() => {
        const getPlayerBoard = async() =>{
            const siren: SirenEntity<IBoardDTO> = await getBoard(validatedGameID, Fleet.MY)
            const boardDTO = siren.properties
          
            const boardChanged =  boardDTO.shots.length !== currentPlayerBoard.shots.length ||
                                  boardDTO.hits.length !== currentPlayerBoard.hits.length

            if(boardChanged){
                setPlayerBoard(toBoard(boardDTO))
                changeTurn()
                clearInterval(intervalID)
            }
        }

        const intervalID = setInterval(() => {
            if(turn === Fleet.OPPONENT){
                getPlayerBoard()
            }
        }, 1000)
    }, [currentOpponentBoard])

    React.useEffect(() => {
        console.log(currentOpponentBoard)
    }, [currentOpponentBoard])

    React.useEffect(() => {
        console.log(currentPlayerBoard)
    }, [currentPlayerBoard])

    return (
        <div>
            <GameView
                playerBoard={currentPlayerBoard}
                opponentBoard={currentOpponentBoard}
                onBoardSquareClick={onSquareClicked}
                timeoutBarPercentage={100}
            />
        </div>
    )
}


