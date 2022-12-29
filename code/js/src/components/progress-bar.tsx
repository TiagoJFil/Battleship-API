import * as React from "react"
import useInterval  from "../hooks/use-interval"
import '../css/timeout-bar.css'
import { styles } from '../constants/styles'

const WARNING_THRESHOLD = 20
const INTERVAL_TIME_MS = 50
const ABOVE_THRESHOLD_COLOR = 'var(--oc-blue-5)'
const BELOW_THRESHOLD_COLOR = 'var(--oc-red-5)'

interface TimerProps {
  timeout: number //in ms
  onTimeout: () => void
}

export function Timer(props: TimerProps){  

    const [percentage, setPercentage] = React.useState(100)
    const [remainingTime, setRemainingTime] = React.useState(props.timeout)
    const [isOver, setIsOver] = React.useState(false)

    useInterval(() => {
      setRemainingTime((prev) => {
          if(prev === 0){ 
              props.onTimeout()         
              setIsOver(true)
              return
          }
          return Math.max(0, prev - INTERVAL_TIME_MS)
      })
      
      const newPercentage = ~~((remainingTime / props.timeout) * 100)
      setPercentage(Math.max(0, newPercentage))

    }, !isOver ? INTERVAL_TIME_MS : null) // Stops the interval when isOver is true


    const fillerStyles = {
      height: '100%',
      width: `${percentage}%`,
      backgroundColor: percentage > WARNING_THRESHOLD ? ABOVE_THRESHOLD_COLOR : BELOW_THRESHOLD_COLOR,
      borderRadius: 'inherit',
      transition: 'width 1s ease-in-out',
    }

    return(
      <div className={styles.TIMEOUT_BAR_CONTAINER}>
        <div style={fillerStyles}> </div>
      </div>
    )
      
}