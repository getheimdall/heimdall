import i18n from "../i18n/i18n"
import {HTTPv1} from "../utils/Http"
import {EnumFilters} from "../utils/EnumFiltersUtils"

const getTrace = (traceId) => {
    if (!traceId) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
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
    const offset = params.params.offset
    const limit = params.params.limit
    const filtersSelected = updateOperationSelectedToEnum(params.params.filtersSelected)
    return HTTPv1.post(`/traces?offset=${offset}&limit=${limit}`, filtersSelected)
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

const updateOperationSelectedToEnum = (filters) => {

    let filtersToSend = [];

    filters.forEach((f) => {
        let filter = {};
        filter['operationSelected'] = EnumFilters[f.operationSelected]
        filter['firstValue'] = f.firstValue
        filter['secondValue'] = f.secondValue
        filter['name'] = f.name
        filter['type'] = f.type

        filtersToSend.push(filter)
    })

    return filtersToSend;
}

export const traceService = {
    getTrace,
    getTraces
}