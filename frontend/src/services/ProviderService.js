import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getProviders = (params = {params: {}}) => {
    return HTTPv1.get('/providers', params)
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

const getProvider = (providerId) => {
    if (isNaN(providerId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/providers/' + providerId)
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

const save = (provider) => {
    return HTTPv1.post('/providers', JSON.stringify(provider))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (provider) => {
    return HTTPv1.put(`/providers/${provider.id}`, JSON.stringify(provider))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (providerId) => {
    return HTTPv1.delete('/providers/' + providerId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const providerService = {
    getProviders,
    getProvider,
    save,
    update,
    remove
}