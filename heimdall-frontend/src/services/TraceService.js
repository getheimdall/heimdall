import {HTTPv1} from "../utils/Http";

const getTrace = (traceId) => {
    if (!traceId) {
        return Promise.reject(new Error('Invalid parameter'))
    }

    return HTTPv1.get('/traces/' + traceId)
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const getTraces = (params = {params: {}}) => {
    return HTTPv1.get('/traces', params)
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const traceService = {
    getTrace,
    getTraces
}