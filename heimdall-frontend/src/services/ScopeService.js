import { HTTPv1 } from '../utils/Http'
import i18n from "../i18n/i18n"

const save = (apiId, scope) => {
    if (isNaN(apiId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.post('/apis/' + apiId + '/scopes', JSON.stringify(scope))
        .then(res => {

            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)

            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('scope_not_found')));
            }
            throw error;
        })
}

const update = (apiId, scope) => {
    if (isNaN(apiId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.put('/apis/' + apiId + '/scopes/' + scope.id, JSON.stringify(scope))
        .then(res => {

            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)

            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('scope_not_found')));
            }
            throw error;
        })
}

const getScopes = (params = { params: {} }, apiId) => {
    return HTTPv1.get('/apis/' + apiId + "/scopes", params)
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const getScope = (apiId, id) => {
    if (isNaN(apiId) || isNaN(id)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + apiId + '/scopes/' + id)
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('resource_not_found')));
            }
            throw error;
        })
}

const remove = (apiId, idScope) => {
    if (isNaN(apiId) || isNaN(idScope)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.delete('/apis/' + apiId + '/scopes/'+ idScope)
    .then(res => Promise.resolve(res.data))
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

export const scopeService = {
    save,
    getScopes,
    update,
    getScope,
    remove
}