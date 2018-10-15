import { HTTP } from '../utils/Http'

const getVersion = () => {
    return HTTP.get('/manager/info')
        .then(result => {
            return Promise.resolve(result.data.app.version)
        })
        .catch(error => {
            console.log(error)
            throw error
        })
}

export const infoService = {
    getVersion,
}