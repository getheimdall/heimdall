import i18n from "../i18n/i18n"
import { ScopeConstants } from '../constants/actions-types'
import { scopeService } from '../services'

export const initLoading = () => dispatch => {
    dispatch({ type: ScopeConstants.SCOPE_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: ScopeConstants.SCOPE_LOADING_FINISH })
}

export const formLoading = (loading) => dispatch => {
    dispatch({ type: ScopeConstants.FORM_LOADING, loading: loading })
}

export const getScopes = (apiId, query = { offset: 0, limit: 10 }) => dispatch => {
    const parameters = { params: query }
    scopeService.getScopes(parameters, apiId)
        .then(data => {
            dispatch({ type: ScopeConstants.GET_SCOPES, scopes: data})
            dispatch(finishLoading())
        })
}

export const clearScopes = () => dispatch => {
    dispatch({ type: ScopeConstants.CLEAR_SCOPES })
}

export const getScope = (apiId, id) => dispatch => {
    dispatch(formLoading(true))
    scopeService.getScope(apiId, id)
    .then(data => {
        dispatch({ type: ScopeConstants.GET_SCOPE,  scope: data })
        dispatch(formLoading(false))
    })
    .catch(error => {
        console.log(error)
        dispatch(formLoading(false))
    })
}

export const clearScope = () => dispatch => {
    dispatch({ type: ScopeConstants.CLEAR_SCOPE })
}

export const save = (apiId, scope) => dispatch => {
    dispatch(initLoading())
    scopeService.save(apiId, scope)
        .then(data => {
            dispatch(getScopes(apiId))
            dispatch(sendNotification({ type: 'success', message: 'Scope saved' }))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const update = (apiId, scope) => dispatch => {
    scopeService.update(apiId, scope)
        .then(data => {
            // dispatch(getDeveloper(developer.id))
            dispatch(getScopes(apiId))
            dispatch(sendNotification({ type: 'success', message: 'Scope updated' }))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: ScopeConstants.SCOPE_NOTIFICATION, notification })
}

export const remove = (apiId, idScope) => dispatch => {
    scopeService.remove(apiId, idScope)
    .then(() => {
        dispatch(getScopes(apiId))
        dispatch(finishLoading())
    })
    .catch(error => {
        console.log(error)
        if (error.response && error.response.status === 400) {
            dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
        }
        dispatch(finishLoading())
    })
}
