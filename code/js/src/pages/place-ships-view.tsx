import * as React from "react"
import '../css/game.css'
import { Board } from "../components/entities/board"
import { BoardView } from "../components/board/board-view"
import { Fleet } from "../components/fleet/fleet-view"
import { FleetControls, FleetState } from "../components/fleet/fleet-view"
import { BoardControls } from "../components/board/board-view"
import { Typography } from "@mui/material"
import { BarColor, CustomProgressBar } from "../components/progress-bar"


interface PlaceShipViewProps{
    board: Board
    boardControls: BoardControls
    fleetState: FleetState
    fleetControls: FleetControls
    timerPercentage: number
}

export function PlaceShipView(
    {
        board,
        boardControls,
        fleetState,
        fleetControls,
        timerPercentage
    }: PlaceShipViewProps
){
    return  (
        <section id="layout-definition-view">
            <div className="layout-definition-view-space">
                <div className="fleet-space">
                    <Fleet
                        state={fleetState}
                        controls={fleetControls}
                    />
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={board}
                        currentShots={[]}
                        controls={boardControls}
                    />
                     <div className="helper-text">
                    <Typography align='center' variant="body1" gutterBottom>To rotate a ship press the right mouse button</Typography>
                </div>
                </div>
               
                <div className="timer-space">
                    <CustomProgressBar progress={timerPercentage} color={BarColor.PRIMARY} />
                </div>
            </div>  
    </section>
    )
}