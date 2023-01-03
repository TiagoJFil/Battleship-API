export interface ModalState{
    message: string;
    isOpen: boolean;
}

export const INITIAL_MODAL_STATE: ModalState = {
    message: "",
    isOpen: false,
}

export const ModalMessages = {
    Finished: "This game is over.",
    Cancelled: "This game has been cancelled due to inactivity.",
    NotLoggedIn: "This action requires you to be logged in. Please log in and try again.",
    Won: "You won!",
    Lost: "You lost!",
}