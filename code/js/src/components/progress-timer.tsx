import * as React from "react"
import useInterval  from "../hooks/use-interval"
import '../css/timeout-bar.css'
import { CustomProgressBar } from "./progress-bar"
import { GameConstants } from "../constants/game"
import { BarColor } from "./progress-bar"

interface TimerProps {
  maxValue: number //in ms
  startValue: number //in ms
  resetToggle: boolean
  barColor: BarColor
  onTimeout: () => void
}

export function ProgressTimer(props: TimerProps){  

    const [percentage, setPercentage] = React.useState(100)
    const [remainingTime, setRemainingTime] = React.useState(props.startValue)
    const [isOver, setIsOver] = React.useState(false)

    useInterval(() => {
      setRemainingTime((prev) => {
          if(prev === null) return prev // Pause the timer when the remainingTime is null
          if(prev === 0){ 
              props.onTimeout()         
              setIsOver(true)
              return
          }
          return Math.max(0, prev - GameConstants.TIMER_PERIOD_MS)
      })
      
      const newPercentage = ~~((remainingTime / props.maxValue) * 100)
      setPercentage(Math.max(0, newPercentage))

    }, !isOver ? GameConstants.TIMER_PERIOD_MS : null) // Stops the interval when isOver is true

    React.useEffect(() => {
      if(!props.resetToggle) return

      setRemainingTime(props.maxValue)
      setIsOver(false)
    }, [props.resetToggle])

    React.useEffect(() => { // Force the timer to reRender when the startValue changes
      setRemainingTime(props.startValue)
    }, [props.startValue])


    return <CustomProgressBar progress={percentage} color={props.barColor} />
      
}