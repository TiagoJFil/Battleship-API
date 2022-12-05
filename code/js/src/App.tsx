import * as React from 'react'
import { Link, Outlet } from 'react-router-dom'
import { BrowserRouter, Route, Routes, } from 'react-router-dom'
import { Statistics } from './components/Ranking/Statistics'
import { Home } from './components/Home/home'
import { Login } from './components/auth/Login/login'
import { Logout } from './components/auth/Logout/logout'
import { Register } from './components/auth/register'
import { Lobby } from './components/auth/Lobby/lobby'
import { Game } from './components/Game/game'


export function App() {
    return (
        <BrowserRouter>
        <Routes>
            <Route path='/' element={<Home/>}>
                <Route path='statistics' element={<Statistics/>}/>
                <Route path='login' element={<Login/>}/>
                <Route path='logout' element={<Logout/>}/>
                <Route path='register' element={<Register/>}/>
                <Route path='lobby' element={<Lobby/>}/>
                <Route path='game/:gameID' element={<Game/>}/>
                </Route>
        </Routes>
    </BrowserRouter> 
    )
}
 