import { AnalyticsConstants } from '../constants/actions-types'
import { analyticsService } from '../services/AnalyticsService'

export const initLoading = () => dispatch => {
    dispatch({ type: AnalyticsConstants.ANALYTICS_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: AnalyticsConstants.ANALYTICS_LOADING_FINISH })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: AnalyticsConstants.ANALYTICS_NOTIFICATION, notification })
}

export const getTopApps = (limit, period) => dispatch => {
    analyticsService.getAppsTop(limit, period)
        .then(data => {
            dispatch({ type: AnalyticsConstants.ANALYTICS_TOP_APPS, topApps: data })
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            dispatch(finishLoading())
        })
}

export const getTopApis = (limit, period) => dispatch => {
    analyticsService.getApisTop(limit, period)
        .then(data => {
            dispatch({ type: AnalyticsConstants.ANALYTICS_TOP_APIS, topApis: data })
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            dispatch(finishLoading())
        })
}

export const getTopAccessTokens = (limit, period) => dispatch => {
    analyticsService.getAccessTokensTop(limit, period)
        .then(data => {
            dispatch({ type: AnalyticsConstants.ANALYTICS_TOP_ACCESS_TOKENS, topAccessTokens: data })
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            dispatch(finishLoading())
        })
}

export const getTopResultStatus = (limit, period) => dispatch => {
    analyticsService.getResultStatusTop(limit, period)
        .then(data => {
            dispatch({ type: AnalyticsConstants.ANALYTICS_TOP_RESULT_STATUS, topResultStatus: data })
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            dispatch(finishLoading())
        })
}