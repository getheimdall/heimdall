import { ApiConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case ApiConstants.RECEIVE_APIS:
            return { ...state, allApis: action.apis }
        case ApiConstants.RESET_API:
            const { api, ...stateWithoutApi } = state
            return { ...stateWithoutApi }
        case ApiConstants.RECEIVE_API:
            return { ...state, api: action.api }
        case ApiConstants.SAVE_API:
            return { ...state, api: action.api }
        case ApiConstants.UPDATE_API:
            return { ...state, api: action.api }
        case ApiConstants.NEW_API:
            return { ...state, api: { status: 'ACTIVE' } }
        case ApiConstants.API_SOURCE:
            return { ...state, apiSource: action.apiSource }
        case ApiConstants.CLEAR_API_SOURCE:
            const { apiSource, ...stateWithoutApiSource } = state
            return { ...stateWithoutApiSource }
        case ApiConstants.FETCHING_API_SOURCE:
            return { ...state, fetching: true }
        case ApiConstants.FINISH_FETCHING_API_SOURCE:
            return { ...state, fetching: false }
        case ApiConstants.API_NOTIFICATION:
            return { ...state, notification: action.notification }
        case ApiConstants.API_LOADING:
            return { ...state, loading: true }
        case ApiConstants.API_LOADING_FINISH:
            return { ...state, loading: false }
        default:
            return state
    }
}