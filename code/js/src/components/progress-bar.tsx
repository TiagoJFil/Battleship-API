import * as React from "react"
import { styles } from "../constants/styles"
import '../css/timeout-bar.css'

export function TimeoutBar(
  props: {
      barPercentage: number
  }
){  
    const fillerStyles = {
        height: '100%',
        width: `${props.barPercentage}%`,
        backgroundColor: props.barPercentage > 20 ? 'var(--oc-blue-5)' : 'var(--oc-red-5)',
        borderRadius: 'inherit',
        transition: 'width 1s ease-in-out',
    }

    return(
      <div className={styles.TIMEOUT_BAR_CONTAINER}>
        <div className={styles.FILLER} style={fillerStyles}> </div>
      </div>
    )
}