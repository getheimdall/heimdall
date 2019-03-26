const decodePayloadAsJson = (token) => {
    const splitToken = token.split(".")[1]
    const base64 = splitToken.replace('-', '+').replace('_', '/');
    return JSON.parse(window.atob(base64))
}

const getTimeToExpiresInSeconds = token => {
    const decode = decodePayloadAsJson(token)
    const dateNow = Date.now() / 1000
    return decode.exp - dateNow
}

export const JwtUtils = {
    decodePayloadAsJson,
    getTimeToExpiresInSeconds
}