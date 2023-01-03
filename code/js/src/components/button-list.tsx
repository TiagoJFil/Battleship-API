import { Button, List } from '@mui/material';
import * as React from "react"



interface ButtonListProps {
    buttons: { name: string; onClick: (e) => void }[];
  }
  
  const ButtonList: React.FC<ButtonListProps> = ({ buttons }) => {
    return (
      <List>
        {buttons.map((button, index) => (
          <Button variant="contained" key={index} onClick={ e => button.onClick(e)}>
            {button.name}
          </Button>
        ))}
      </List>
    );
  };

 export default ButtonList;