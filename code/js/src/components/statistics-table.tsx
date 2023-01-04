import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableContainer from '@mui/material/TableContainer';
import { INamedPlayerStatisticsDTO, IStatisticsDTO } from '../interfaces/dto/statistics-dto';

export function StatisticsTable(
    props : {
        headers : string[],
        data : INamedPlayerStatisticsDTO[]
    }
){
    return(
        <TableContainer style={{ maxHeight: 500 }}>
        <Table stickyHeader>
            <TableHead>
            <TableRow style={{ 'backgroundColor': '#f5f5f5', "height": '35px' }}>
                {
                    props.headers.map((header) => {
                        return (
                            <TableCell key={header}>{header}</TableCell>
                        )
                    })
                }
            </TableRow>
            </TableHead>
            <TableBody>
            {props.data.map(data => {
                return (
                <TableRow key={data.rank}>
                    <TableCell style={{ width: 80 }} >{data.rank}</TableCell>
                    <TableCell style={{ width: 160 }}>{data.playerName}</TableCell>
                    <TableCell style={{ width: 160 }}>{data.totalGames}</TableCell>
                    <TableCell style={{ width: 160 }}>{data.wins}</TableCell>
                </TableRow>
                );
            })}
            </TableBody>
        </Table>
        </TableContainer>
    )
}