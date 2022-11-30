import * as React from 'react'
import { Link, Outlet } from 'react-router-dom'

export function App() {
    return (
        <div>
            <h1>Home</h1>
            <nav>
                <li><Link to="/statistics">Statistics</Link></li>
                <li><Link to="/system-info">System Information</Link></li>
            </nav>
            <Outlet/>
        </div>
    )
}
 