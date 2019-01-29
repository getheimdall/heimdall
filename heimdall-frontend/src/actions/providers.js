import { push } from 'connected-react-router'

import i18n from "../i18n/i18n"
import { providerService } from "../services/ProviderService"
import { ProviderConstants } from '../constants/actions-types'

export const initLoading = () => dispatch => {
    dispatch({ type: ProviderConstants.PROVIDER_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: ProviderConstants.PROVIDER_LOADING_FINISH })
}

export const getAllProviders = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = { params: query }
    providerService.getProviders(parameters)
        .then(data => {
            dispatch({ type: ProviderConstants.GET_PROVIDERS, providers: data })
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const clearProviders = () => dispatch => {
    dispatch({ type: ProviderConstants.CLEAR_PROVIDER })
}

export const clearProvider = () => dispatch => {
    dispatch({ type: ProviderConstants.CLEAR_PROVIDER })
}

export const getProvider = providerId => dispatch => {
    providerService.getProvider(providerId)
        .then(data => {
            dispatch({ type: ProviderConstants.GET_PROVIDER, provider: data })
            dispatch(finishLoading())
        })
        .catch(error => console.log(error))
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: ProviderConstants.PROVIDER_NOTIFICATION, notification })
}

export const save = provider => dispatch => {
    providerService.save(provider)
        .then(data => {
            dispatch(sendNotification({ type: 'success', message: i18n.t('provider_saved') }))
            dispatch(push('/providers'))
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

export const update = provider => dispatch => {
    providerService.update(provider)
        .then(data => {
            dispatch(getProvider(provider.id))
            dispatch(sendNotification({ type: 'success', message: i18n.t('provider_updated') }))
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const remove = providerId => dispatch => {
    providerService.remove(providerId)
        .then(data => {
            dispatch(getAllProviders())
            dispatch(sendNotification({ type: 'success', message: i18n.t('provider_removed') }))
            dispatch(finishLoading())
        })
}

