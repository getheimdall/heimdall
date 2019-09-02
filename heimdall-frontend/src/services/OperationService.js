import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getOperationsByResource = (idApi, idResource) => {
    if (isNaN(idApi) || isNaN(idResource)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + idApi + '/resources/' + idResource + '/operations')
        .then(res => {

            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('operation_not_found')));
            }
            throw error;
        })
}

const getOperationsByApi = (idApi) => {
    if (isNaN(idApi)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + idApi + '/operations')
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('operation_not_found')));
            }
            throw error;
        })
}

const getOperation = (idApi, idResource, idOperation) => {
    if (isNaN(idApi) || isNaN(idResource) || isNaN(idOperation)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + idApi + '/resources/' + idResource + '/operations/' + idOperation)
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('operation_not_found')));
            }
            throw error;
        })
}

const save = (idApi, idResource, operation) => {
    if (isNaN(idApi) || isNaN(idResource)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.post('/apis/' + idApi + '/resources/' + idResource + '/operations', JSON.stringify(operation))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('operation_not_found')));
            }
            throw error;
        })
}

const remove = (idApi, idResource, idOperation) => {
    if (isNaN(idApi) || isNaN(idResource) || isNaN(idOperation)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.delete('/apis/' + idApi + '/resources/' + idResource + '/operations/' + idOperation)
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('operation_not_found')));
            }
            throw error;
        })
}

const update = (idApi, idResource, operation) => {
    if (isNaN(idApi) || isNaN(idResource)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.put('/apis/' + idApi + '/resources/' + idResource + '/operations/' + operation.id, JSON.stringify(operation))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return Promise.reject(new Error(i18n.t('operation_not_found')));
            }
            throw error;
        })
}

export const operationService = {
    getOperationsByResource,
    getOperationsByApi,
    getOperation,
    save,
    remove,
    update
}