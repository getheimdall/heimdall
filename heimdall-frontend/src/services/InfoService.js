import { HTTP } from '../utils/Http'

const getVersion = () => {
   return getManagerInfo().then(data => {
       return data.app.version;
   })
}

const getManagerInfo = () => {
    return HTTP.get('/manager/info')
        .then(result => {
            return Promise.resolve(result.data)
        })
        .catch(error => {
            console.log(error)
            throw error
        })
}

export const infoService = {
    getVersion,
    getManagerInfo
}