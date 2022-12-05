import * as React from 'react'
import { createRoot } from 'react-dom/client'
import { App } from './app'

const root = createRoot(document.getElementById("the-div"))

root.render(
    <App />   
)
