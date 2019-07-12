import { HTTPv1 } from '../utils/Http'

const clearCaches = (params = {params: {}}) => {
    return HTTPv1.delete('/caches', params)
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const cacheService = {
    clearCaches
}