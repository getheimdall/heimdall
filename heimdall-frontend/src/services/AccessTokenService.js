import i18n from '../i18n/i18n'
import { HTTPv1 } from '../utils/Http'

const getAccessTokens = (params = {params: {}}) => {
    return HTTPv1.get('/access_tokens', params)
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

const getAccessToken = (accessTokenId) => {
    if (isNaN(accessTokenId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/access_tokens/' + accessTokenId)
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

const save = (accessToken) => {
    return HTTPv1.post('/access_tokens', JSON.stringify(accessToken))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (accessToken) => {
    return HTTPv1.put('/access_tokens/' + accessToken.id, JSON.stringify(accessToken))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (accessTokenId) => {
    return HTTPv1.delete('/access_tokens/' + accessTokenId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const accessTokenService = {
    getAccessTokens,
    getAccessToken,
    save,
    update,
    remove
}