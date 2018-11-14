import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getInterceptors = (params = {params: {}}) => {
    return HTTPv1.get('/interceptors', params)
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

const getInterceptorTypes = () => {
    return HTTPv1.get('/interceptors/types')
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

const getInterceptor = (interceptorId) => {
    if (isNaN(interceptorId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/interceptors/' + interceptorId)
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

const save = (interceptor) => {
    return HTTPv1.post('/interceptors', JSON.stringify(interceptor))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (interceptor) => {
    return HTTPv1.put('/interceptors/' + interceptor.id, JSON.stringify(interceptor))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (interceptorId) => {
    return HTTPv1.delete('/interceptors/' + interceptorId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const interceptorService = {
    getInterceptors,
    getInterceptorTypes,
    getInterceptor,
    save,
    update,
    remove
}