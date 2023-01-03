import * as React from 'react'
import { BrowserRouter, Navigate, Route, Routes, } from 'react-router-dom'
import { Statistics } from './pages/statistics-view'
import { Home } from './pages/home-view'
import { Logout } from './components/auth/logout/logout'
import { Register } from './components/auth/register'
import { Lobby } from './core-ui/lobby'
import { PlaceShips } from './core-ui/place-ships'
import { Game } from './core-ui/game'
import { Info } from './components/info'
import { UserGames } from './pages/user-games-view'
import { Outlet } from 'react-router-dom'
import { BottomNav } from './components/bottom-nav'
import { ToastContainer } from 'react-toastify';
import "./css/app.css"
import NotFound from './pages/not-found-view'
import { Login } from './pages/login-view'

export function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<Outlet/>}>
                    <Route path='/' element={<Home/>}/>
                    <Route path='statistics' element={<Statistics/>}/>
                    <Route path='login' element={<Login/>}/>
                    <Route path='logout' element={<Logout/>}/>
                    <Route path='register' element={<Register/>}/>
                    <Route path='lobby' element={<Lobby/>}/>
                    <Route path='game/:gameID/layout-definition' element={<PlaceShips/>}/>
                    <Route path='game/:gameID' element={<Game/>}/>
                    <Route path='info' element={<Info/>}/>
                    <Route path='my/games' element={<UserGames/>}/>
                    <Route path='/not-found' element={<NotFound/>}/>
                    <Route path='*' element={<Navigate replace to="/not-found" />}/>
                </Route>
            </Routes>
            <ToastContainer/>
            <BottomNav/>
        </BrowserRouter> 
    )
}
 