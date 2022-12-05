import * as React from 'react'

export function useRetryUntilResolved(callback, interval = 100, hasResolved, setAsResolved) {
    const intervalID = setInterval(
      () => {
        const result = callback();
        console.log("result is", result)
        if (result) {
            setAsResolved();
        }
        console.log("hasResolved is", hasResolved.current)

        if(hasResolved.current){
            console.log("clearing interval")
            clearInterval(intervalID);
            
        }
      },
        interval
    );
   
    return hasResolved.current;
}



function useInterval(callback, delay) {
    const intervalRef = React.useRef(null);
    const savedCallback = React.useRef(callback);

    React.useEffect(() => {
      savedCallback.current = callback;
    }, [callback]);

    React.useEffect(() => {
      const tick = () => savedCallback.current();

      if (typeof delay === 'number') {
        intervalRef.current = window.setInterval(tick, delay);
        
        return () => window.clearInterval(intervalRef.current);
      }
    }, [delay]);

    return intervalRef;
  }