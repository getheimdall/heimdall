import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getApis = (params = {params: {}}) => {
    return HTTPv1.get('/apis', params)
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

const getApiById = (id) => {

    if (isNaN(id)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + id)
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const updateApi = (api) => {
    // api = JSON.stringify(api)
    return HTTPv1.put('/apis/' + api.id, JSON.stringify(api))
    .then(res => {
        console.log(res)
        // if (res.status >= 200 && res.status < 300) {
        //     return Promise.reject(res.statusText)
        // }

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const saveApi = (api) => {
    
    api = JSON.stringify(api)
    return HTTPv1.post('/apis', api)
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const deleteApi = id => {
    if (isNaN(id)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.delete('/apis/' + id)
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

export const apiService = {
    getApis,
    getApiById,
    updateApi,
    saveApi,
    deleteApi
}