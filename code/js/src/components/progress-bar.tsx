import * as React from "react"
import { useNavigate } from "react-router-dom"
import { styles } from "../constants/styles"
import '../css/timeout-bar.css'

const WARNING_THRESHOLD = 20
const INTERVAL_TIME_MS = 1000
const ABOVE_THRESHOLD_COLOR = 'var(--oc-blue-5)'
const BELOW_THRESHOLD_COLOR = 'var(--oc-red-5)'
const HOME_URI_SUFFIX = '/'

export function TimeoutBar(
  props: {
      timeout: number //in ms   
  }
){  
    const navigate = useNavigate();
    const timeoutSeconds = props.timeout / 1000

    const [percentage, setPercentage] = React.useState(100)
    const [remainingTime, setRemainingTime] = React.useState(null)

    React.useEffect(() => {
      setRemainingTime(timeoutSeconds)
      const intervalID = setInterval(() => {
          
          setRemainingTime((prev) => {
              if(prev == 0){ 
                  //TIMEOUT
                  navigate(HOME_URI_SUFFIX)        
                  clearInterval(intervalID)
                  return
              }
              return prev - 1
          })
          
          setPercentage((previousPercentage) => {
              return previousPercentage > 0 ? 
                        previousPercentage - 100 / timeoutSeconds : 
                        0
          })
      }, INTERVAL_TIME_MS)

      return () => {
          clearInterval(intervalID)
      }
    }, [])


    const fillerStyles = {
      height: '100%',
      width: `${percentage}%`,
      backgroundColor: percentage > WARNING_THRESHOLD ? ABOVE_THRESHOLD_COLOR : BELOW_THRESHOLD_COLOR,
      borderRadius: 'inherit',
      transition: 'width 1s ease-in-out',
    }

    return(
      <div className={styles.TIMEOUT_BAR_CONTAINER}>
        <div className={styles.FILLER} style={fillerStyles}> </div>
      </div>
    )
}