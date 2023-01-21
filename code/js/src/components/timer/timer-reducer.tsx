import { GameConstants } from "../../constants/game"

export type TimerState = 
{
  state : "stopped",
  currentValue: number,
}
|
{
  state: "running",
  currentValue: number,
}
|
{
  state: "finished",
  currentValue: number
}

export type TimerAction =
{
  type: "start",
  startValue: number
}
|
{
  type: "continue"
}
|
{
  type: "stop",
}
|
{
  type: "update",
}
|
{
  type: "reset",
  startValue: number
}

export function timerReducer (state: TimerState, action: TimerAction) {
    switch(action.type){
        case "start":
            if( state.state === "stopped" ){
                return {state : "running", currentValue: action.startValue }
            }
            else{
                return state
            }
        case "reset":
            return {state: "running", currentValue: action.startValue}
        case "continue":
                if(state.state === "stopped"){
                    return {state: "running", currentValue: state.currentValue}
                }
                else{
                    return state
                }
        case "stop":

            return {state: "stopped", currentValue: state.currentValue}
        case "update":
            if(state.state === "finished" || state.state === "stopped"){
                return state
            }
            if(state.state === "running" && state.currentValue > 0){
                state.currentValue = Math.max(0, state.currentValue - GameConstants.TIMER_PERIOD_MS)
            }
            if(state.state === "running" && state.currentValue === 0){

                return {state: "finished", currentValue: state.currentValue}
            }
            
            return {state: "running", currentValue: state.currentValue}  
        default:
            return {state: state, currentValue: state.currentValue }
    }
}