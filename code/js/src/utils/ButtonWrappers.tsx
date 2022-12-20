export async function DisableButtonWhileOnClickWrapper(e : any ,block : () => any)  {
    try {
        e.target.disabled = true;
        await block();
    } finally {
        e.target.disabled = false;
    }
}