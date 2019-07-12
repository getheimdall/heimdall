import { notification } from 'antd'
import { push } from 'connected-react-router'

import i18n from "../../i18n/i18n"
import { appService } from '../../services'
import { AppConstants } from '../../constants/actions-types'

export const initLoading = () => dispatch => {
    dispatch({ type: AppConstants.APP_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: AppConstants.APP_LOADING_FINISH })
}

export const getAllApps = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = { params: query }
    appService.getApps(parameters)
        .then(data => {
            dispatch({ type: AppConstants.GET_APPS, apps: data })
            dispatch(finishLoading())
        })
}

export const clearApps = () => dispatch => {
    dispatch({ type: AppConstants.CLEAR_APPS })
}

export const getApp = appId => dispatch => {
    appService.getApp(appId)
        .then(data => dispatch({ type: AppConstants.GET_APP, app: data }))
        .catch(error => console.log(error))
}

export const clearApp = () => dispatch => {
    dispatch({ type: AppConstants.CLEAR_APP })
}

export const save = app => dispatch => {
    appService.save(app)
        .then(data => {
            dispatch(getAllApps())
            notification['success']({ message:  i18n.t('app_saved') })
            dispatch(push('/apps'))
            dispatch(finishLoading())
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                notification['error']({ message: i18n.t('error'), description: error.response.data.message })
            }
            dispatch(finishLoading())
        })
}

export const update = app => dispatch => {
    appService.update(app)
        .then(data => {
            dispatch(getApp(app.id))
            notification['success']({ message: i18n.t('app_updated') })
            dispatch(finishLoading())
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                notification['error']({ message: i18n.t('error'), description: error.response.data.message })
            }
            dispatch(finishLoading())
        })
}

export const remove = (appId, query) => dispatch => {
    appService.remove(appId)
        .then(data => {
            dispatch(getAllApps(query))
            notification['success']({ message: i18n.t('app_removed') })
        })
}

export const appSource = appSource => dispatch => {
    dispatch({ type: AppConstants.APP_SOURCE, appSource })
}

export const clearAppSource = () => dispatch => {
    dispatch({ type: AppConstants.CLEAR_APP_SOURCE })
}

export const fetchingApp = () => dispatch => {
    dispatch({ type: AppConstants.FETCHING_APP_SOURCE })
}

export const finishFetchingApp = () => dispatch => {
    dispatch({ type: AppConstants.FINISH_FETCHING_APP_SOURCE })
}

export const getAppSourceByName = name => dispatch => {
    const parameters = { params: { name: name } }
    appService.getApps(parameters)
        .then(data => {
            dispatch(appSource(data))
            dispatch(finishFetchingApp())
        })
}

