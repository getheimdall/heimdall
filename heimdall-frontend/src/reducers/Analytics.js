import {AnalyticsConstants} from "../constants/actions-types"

export default (state = {}, action) => {
    switch (action.type) {
        case AnalyticsConstants.ANALYTICS_LOADING:
            return { ...state, loading: true }
        case AnalyticsConstants.ANALYTICS_LOADING_FINISH:
            return { ...state, loading: false }
        case AnalyticsConstants.ANALYTICS_TOP_APPS:
            return { ...state, topApps: action.topApps }
        case AnalyticsConstants.ANALYTICS_TOP_APIS:
            return { ...state, topApis: action.topApis }
        case AnalyticsConstants.ANALYTICS_TOP_ACCESS_TOKENS:
            return { ...state, topAccessTokens: action.topAccessTokens }
        case AnalyticsConstants.ANALYTICS_TOP_RESULT_STATUS:
            return { ...state, topResultStatus: action.topResultStatus }
        case AnalyticsConstants.ANALYTICS_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}