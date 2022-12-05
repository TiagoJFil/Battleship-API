import * as React from 'react'
import { Link, Outlet } from 'react-router-dom'
import { isLoggedIn } from '../../api/session'

export function Home(){
    return(
    
        <div>
        <h1>Home</h1>
        <nav>
            <li><Link to="/statistics">Statistics</Link></li>
            <li><Link to="/system-info">System Information</Link></li>
            <li><Link to="/login">Login</Link></li>
            <li><Link to="/logout">Logout</Link></li>
            <li><Link to="/register">Register</Link></li>
            <li><Link to="/lobby">Join Queue</Link></li>
            </nav>
        <Outlet/>
    </div>)
}