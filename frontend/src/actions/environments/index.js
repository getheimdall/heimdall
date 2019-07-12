import { push } from 'connected-react-router'

import i18n from "../../i18n/i18n"
import { EnvironmentConstants } from '../../constants/actions-types'
import { environmentService } from '../../services'

const getEnvironments = (environments) => ({
    type: EnvironmentConstants.GET_ENVIRONMENTS,
    environments
})

export const getAllEnvironments = () => dispatch => {
    environmentService.getEnvironments()
        .then(data => {
            dispatch(getEnvironments(data))
            dispatch(finishLoading())
        })
        .catch(error => console.log(error))
}

export const clearEnvironments = () => dispatch => {
    dispatch({ type: EnvironmentConstants.CLEAR_ENVIRONMENTS })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: EnvironmentConstants.ENVIRONMENT_NOTIFICATION, notification })
}

export const getEnvironment = idEnvironment => dispatch => {
    environmentService.getEnvironment(idEnvironment)
        .then(data => dispatch({ type: EnvironmentConstants.GET_ENVIRONMENT, environment: data }))
        .catch(error => console.log(error))
}

export const save = environment => dispatch => {
    environmentService.save(environment)
        .then(data => {
            dispatch(getAllEnvironments())
            dispatch(sendNotification({ type: 'success', message: i18n.t('environment_saved') }))
            dispatch(push('/environments'))
            dispatch(finishLoading())
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const update = environment => dispatch => {
    environmentService.update(environment)
        .then(data => {
            dispatch(getEnvironment(environment.id))
            dispatch(sendNotification({ type: 'success', message: i18n.t('environment_updated') }))
            dispatch(finishLoading())
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(getEnvironment(environment.id))
            dispatch(finishLoading())
        })
}

export const remove = environmentId => dispatch => {
    environmentService.remove(environmentId)
        .then(data => {
            dispatch(getAllEnvironments())
            dispatch(sendNotification({ type: 'success', message: i18n.t('environment_removed') }))
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(getAllEnvironments())
            dispatch(finishLoading())
        })
}

export const clearEnvironment = () => dispatch => {
    dispatch({ type: EnvironmentConstants.CLEAR_ENVIRONMENT })
}

export const initLoading = () => dispatch => {
    dispatch({ type: EnvironmentConstants.ENVIRONMENT_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: EnvironmentConstants.ENVIRONMENT_LOADING_FINISH })
}