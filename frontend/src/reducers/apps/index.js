import { AppConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case AppConstants.GET_APPS:
            return { ...state, apps: action.apps }
        case AppConstants.CLEAR_APPS:
            const { apps, ...stateWithoutApps } = state
            return { ...stateWithoutApps }
        case AppConstants.GET_APP:
            return { ...state, app: action.app }
        case AppConstants.CLEAR_APP:
            const { app, ...stateWithoutApp } = state
            return { ...stateWithoutApp }
        case AppConstants.APP_LOADING:
            return { ...state, loading: true }
        case AppConstants.APP_LOADING_FINISH:
            return { ...state, loading: false }
        case AppConstants.APP_NOTIFICATION:
            return { ...state, notification: action.notification }
        case AppConstants.APP_SOURCE:
            return { ...state, appSource: action.appSource }
        case AppConstants.CLEAR_APP_SOURCE:
            const { appSource, ...stateWithoutAppSource } = state
            return { ...stateWithoutAppSource }
        case AppConstants.FETCHING_APP_SOURCE:
            return { ...state, fetching: true }
        case AppConstants.FINISH_FETCHING_APP_SOURCE:
            return { ...state, fetching: false }
        default:
            return state
    }
}