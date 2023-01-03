import * as React from "react"
import useInterval  from "../hooks/use-interval"
import '../css/timeout-bar.css'
import { CustomProgressBar } from "./progress-bar"
import { GameConstants } from "../constants/game"

interface TimerProps {
  maxValue: number //in ms
  startValue: number //in ms
  resetToggle: boolean
  onTimeout: () => void
}

export function ProgressTimer(props: TimerProps){  

    const [percentage, setPercentage] = React.useState(100)
    const [remainingTime, setRemainingTime] = React.useState(props.startValue)
    const [isOver, setIsOver] = React.useState(false)

    useInterval(() => {
      setRemainingTime((prev) => {
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
      setRemainingTime(props.maxValue)
      setIsOver(false)
    }, [props.resetToggle])


    return <CustomProgressBar progress={percentage} />
      
}