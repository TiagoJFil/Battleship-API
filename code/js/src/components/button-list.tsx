import { Button, List } from '@mui/material';
import * as React from "react"
import { Typography } from "@mui/material";
import { IGameStateInfoDTO } from '../interfaces/dto/game-state-dto';
import { IUserDTO } from '../interfaces/dto/user-dto';
import { GameState } from './entities/game-state';



interface ButtonListProps {
    buttons: {  info: {
      gameID: number;
      stateWithPlayers: {
          stateInfo: IGameStateInfoDTO;
          player1: IUserDTO;
          player2: IUserDTO;
      };
  },
   onClick: (e) => void }[];
  }
  
  const ButtonList: React.FC<ButtonListProps> = ({ buttons }) => {
    return (
      <div className='buttonList'>
        {buttons.map((button, index) => (
          
          <div className="GameContainer" key={button.info.gameID}>
            <Typography variant = "body1">{button.info.stateWithPlayers.player1.name} vs {button.info.stateWithPlayers.player2.name}</Typography>
            <Button variant="contained" key={index} onClick={ e => button.onClick(e)}>
              {GameState[button.info.stateWithPlayers.stateInfo.state]} 
            </Button>
          </div>
        ))}
      </div>
    );
  };

 export default ButtonList;