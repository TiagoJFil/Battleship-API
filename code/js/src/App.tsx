import * as React from 'react'
import { BrowserRouter, Route, Routes, } from 'react-router-dom'
import { Statistics } from './pages/statistics-view'
import { Home } from './pages/home-view'
import { Login } from './components/auth/login/login'
import { Logout } from './components/auth/logout/logout'
import { Register } from './components/auth/register'
import { Lobby } from './components/auth/lobby/lobby'
import { PlaceShips } from './core-ui/place-ships'
import { Game } from './core-ui/game'


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
                <Route path='game/:gameID/layout-definition' element={<PlaceShips/>}/>
                <Route path='game/:gameID' element={<Game/>}/>
                </Route>
        </Routes>
    </BrowserRouter> 
    )
}
 