import * as React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Route, Routes} from 'react-router-dom'
import { App } from './App'
import { Statistics } from './Statistics'

const root = createRoot(document.getElementById("the-div"))

root.render(
    <BrowserRouter>
    <Routes>
        <Route path='/' element={<App/>}>
            <Route path='statistics' element={<Statistics/>}/>
        </Route>
    </Routes>
  </BrowserRouter>,   
)

