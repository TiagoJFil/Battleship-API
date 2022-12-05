import * as React from 'react';
import '../../css/board.css';
import { SquareType } from '../game/Game';
import { Ship } from '../utils/Ship';
import * as BoardUtils from './utils';

interface Props{
    tiles: SquareType[],
    setCurrentlyPlacing: (value: Ship) => void,
    currentlyPlacing: Ship,
    placeShip: (ship: Ship) => void,
    placedShips: Ship[],
    rotateShip: React.MouseEventHandler<HTMLDivElement>
}

export function PlayerBoard(
    props: Props
){
    const initialTiles = props.tiles
    const boardSide = Math.sqrt(initialTiles.length)

    let tiles = props.placedShips.length > 0 ? Array.from(props.placedShips.reduce((prevBoard, currentShip) =>
        BoardUtils.putShipInBoard(prevBoard, boardSide, currentShip, SquareType.ship_part),
        initialTiles
      )) : initialTiles;
    
    const isPlacingOverBoard =  props.currentlyPlacing && props.currentlyPlacing.position != null;
    const canPlaceCurrentShip = isPlacingOverBoard && BoardUtils.canBePlaced(props.currentlyPlacing, tiles, boardSide);
    
    if (isPlacingOverBoard) {
        if (canPlaceCurrentShip) {
            tiles = BoardUtils.putShipInBoard(tiles, boardSide, props.currentlyPlacing, SquareType.ship_part);
        } else {
            const invalidShip = new Ship(
                props.currentlyPlacing.size - BoardUtils.calculateOverhang(props.currentlyPlacing, boardSide),
                props.currentlyPlacing.orientation,
                props.currentlyPlacing.position,
                props.currentlyPlacing.placed
            )
            tiles = BoardUtils.putShipInBoard(tiles, boardSide, invalidShip, SquareType.forbidden);
          //console.log("Can't place ship here"); //TODO
        }
      }
    
    const squares = tiles.map((type, index) => {
        return(
            <div
                onMouseDown={props.rotateShip}
                onClick={() => {
                    if(canPlaceCurrentShip){
                        props.placeShip(props.currentlyPlacing)
                    }
                }}
                className={`square ${BoardUtils.typeToClass[type]}`}
                key={`square-${index}`}
                id={`square-${index}`}
                onMouseOver={() => {
                    if (props.currentlyPlacing) {
                      props.setCurrentlyPlacing({
                        ...props.currentlyPlacing,
                        position: BoardUtils.indexToPosition(index, boardSide),
                      });
                    }
                  }}
            />
        )
        
    })

    const disableContextMenu = (e: {preventDefault: () => void}) => {
        e.preventDefault();

    }
    return (
        <div className="board" onContextMenu= {disableContextMenu}>
            {squares}
        </div>
    )
}


