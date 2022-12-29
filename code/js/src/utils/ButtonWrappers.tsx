
export async function executeWhileDisabled(button: HTMLButtonElement, block: () => Promise<void>) {
    button.disabled = true;
    await block()
    button.disabled = false;
}