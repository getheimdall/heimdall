import { CacheConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case CacheConstants.CACHE_LOADING:
            return { ...state, loading: true }
        case CacheConstants.CACHE_LOADING_FINISH:
            return { ...state, loading: false }
        case CacheConstants.CACHE_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}