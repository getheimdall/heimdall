import { DeveloperConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case DeveloperConstants.GET_DEVELOPERS:
            return { ...state, developers: action.developers }
        case DeveloperConstants.DEVELOPER_LOADING:
            return { ...state, loading: true }
        case DeveloperConstants.GET_DEVELOPER:
            return { ...state, developer: action.developer }
        case DeveloperConstants.CLEAR_DEVELOPER:
            const { developer, ...stateWithoutDev } = state
            return { ...stateWithoutDev }
        case DeveloperConstants.DEVELOPER_LOADING_FINISH:
            return { ...state, loading: false }
        case DeveloperConstants.DEVELOPER_SOURCE:
            return { ...state, developerSource: action.developerSource }
        case DeveloperConstants.CLEAR_DEVELOPER_SOURCE:
            const { developerSource, ...stateWithoutDevSource } = state
            return { ...stateWithoutDevSource }
        case DeveloperConstants.FETCHING_DEVELOPER_SOURCE:
            return { ...state, fetching: true }
        case DeveloperConstants.FINISH_FETCHING_DEVELOPER_SOURCE:
            return { ...state, fetching: false }
        case DeveloperConstants.DEVELOPER_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}