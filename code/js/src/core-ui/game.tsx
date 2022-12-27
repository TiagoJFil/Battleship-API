import * as React from 'react'
import { useParams } from 'react-router-dom'
import { Square } from '../components/entities/square'
import { Fleet } from '../components/entities/fleet'
import { GameView } from '../pages/game-view'
import { EmbeddedEntity, SirenEntity } from '../interfaces/hypermedia/siren'
import '../css/board.css'
import { defineShot, getBoard, getGameRules, getGameState } from '../api/api'
import { SquareType } from '../components/entities/square-type'
import { IBoardDTO, toBoard } from '../interfaces/dto/board-dto'
import { Board } from '../components/entities/board'
import { IGameStateInfoDTO } from '../interfaces/dto/game-state-dto'
import { getCookie, UID_COOKIE_NAME } from '../api/auth'
import { IGameRulesDTO } from '../interfaces/dto/game-rules-dto'
import { GameState } from '../components/entities/game-state'

const INTERVAL_TIME_MS = 1000

export function Game() {
    let { gameID } = useParams()
    const userID = getCookie(UID_COOKIE_NAME)
   
    const validatedUserID = parseInt(userID)
    const validatedGameID = parseInt(gameID)

    const [loading, setLoading] = React.useState(true)
    const [currentPlayerBoard, setPlayerBoard] = React.useState<Board>(null)
    const [currentOpponentBoard, setOpponentBoard] = React.useState<Board>(null)
    const [shotsDefinitionTimeout, setShotsDefinitionTimeout] = React.useState(null)
    const [turn, setTurn] = React.useState<Fleet>(null) 
    
    React.useEffect(() => {
        const getGameInfo = async () => {
            const playerBoardResponse: SirenEntity<IBoardDTO> = await getBoard(validatedGameID, Fleet.MY)
            const opponentBoardResponse: SirenEntity<IBoardDTO> = await getBoard(validatedGameID, Fleet.OPPONENT)

            const gameStateResponse: SirenEntity<IGameStateInfoDTO> = await getGameState(validatedGameID)

            const playerBoardDTO = playerBoardResponse.properties 
            const opponentBoardDTO = opponentBoardResponse.properties 
            

            setPlayerBoard(toBoard(playerBoardDTO))
            setOpponentBoard(toBoard(opponentBoardDTO))

            if(shotsDefinitionTimeout === null){
                const gameRulesResponse: SirenEntity<IGameRulesDTO> = await getGameRules(validatedGameID)
                const gameRulesDTO = gameRulesResponse.properties
                setShotsDefinitionTimeout(gameRulesDTO.playTimeout)
            }

            const gameStateDTO = gameStateResponse.properties
            const player1ID = gameStateDTO.player1ID

            player1ID === validatedUserID ? setTurn(Fleet.MY) : setTurn(Fleet.OPPONENT)
            setLoading(false)
        }

        getGameInfo()
    }, [])


    const changeTurn = () => {
        setTurn((prevTurn) =>{
            return prevTurn === Fleet.MY ? Fleet.OPPONENT : Fleet.MY
        })
    };

    const onSquareClicked = (squareClicked: Square) => {
        const boardRepresentation = currentOpponentBoard.asMap()

        const squareID = squareClicked.toID()
        const squareType = boardRepresentation.get(squareID) ?? SquareType.WATER
        
        if(turn !== Fleet.MY) return   //TODO FIX
        if(squareType !== SquareType.WATER) return

        const makeShot = async () => {
            const squareDTO = squareClicked.toDTO()
            const shots = [squareDTO]
            
            const sirenResponse = await defineShot(validatedGameID, shots)

            const embeddedEntity = sirenResponse.entities[0] as EmbeddedEntity<IBoardDTO>
            const embeddedBoardDTO = embeddedEntity.properties
        
            setOpponentBoard(toBoard(embeddedBoardDTO))

            const gameInfo = await getGameState(validatedGameID)
            const gameInfoDTO = gameInfo.properties

            if(gameInfoDTO.state === GameState.FINISHED){
                const winner = gameInfoDTO.turnID === validatedUserID ? Fleet.OPPONENT : Fleet.MY //Last to play
                alert(`Game finished! Winner is ${winner}`) //CHANGE TO MODAL
            }
           
            changeTurn()
        }
        
        makeShot()
    }

    React.useEffect(() => {
        if(loading || turn !== Fleet.OPPONENT) return
        
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
        }, INTERVAL_TIME_MS)
    }, [turn])


    React.useEffect(() => {
        console.log('curr turn:' + turn)
    }, [turn])

    return (
        <div>
            <GameView
                loading={loading}
                playerBoard={currentPlayerBoard}
                opponentBoard={currentOpponentBoard}
                onBoardSquareClick={onSquareClicked}
                shotsDefinitionTimeout={shotsDefinitionTimeout}
            />
        </div>
    )
}


