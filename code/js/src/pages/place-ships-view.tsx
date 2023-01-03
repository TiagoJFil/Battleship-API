import * as React from "react"
import '../css/game.css'
import { Board } from "../components/entities/board"
import { BoardView } from "../components/board/board-view"
import { Fleet } from "../components/fleet/fleet-view"
import { ProgressTimer } from "../components/progress-timer"
import { FleetControls, FleetState } from "../components/fleet/fleet-view"
import { BoardControls } from "../components/board/board-view"
import { CircularProgress, Typography } from "@mui/material"
import { BarColor } from "../components/progress-bar"


interface PlaceShipViewProps{
    board: Board
    boardControls: BoardControls
    loading: boolean
    fleetState: FleetState
    fleetControls: FleetControls
    layoutDefinitionTimeout: number
    layoutDefinitionRemainingTimeMs: number
    timerResetToggle: boolean
    onTimeout: () => void
}

export function PlaceShipView(
    {
        board,
        boardControls, 
        loading,
        layoutDefinitionTimeout,
        layoutDefinitionRemainingTimeMs,
        fleetState,
        fleetControls,
        timerResetToggle,
        onTimeout
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
                    <ProgressTimer
                        maxValue={layoutDefinitionTimeout}
                        startValue={layoutDefinitionRemainingTimeMs}
                        resetToggle={timerResetToggle}
                        barColor={BarColor.PRIMARY}
                        onTimeout={onTimeout}
                    /> 
                </div>
            </div>  
    </section>
    )
}