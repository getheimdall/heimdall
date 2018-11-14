const decodePayloadAsJson = (token) => {
    const splitToken = token.split(".")[1]
    const base64 = splitToken.replace('-', '+').replace('_', '/');
    return JSON.parse(window.atob(base64))
}

export const JwtUtils = {
    decodePayloadAsJson
}