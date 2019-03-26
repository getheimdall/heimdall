import { SessionConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case SessionConstants.SESSION_RENEW_TIME:
            return { ...state, time: action.time }
        case SessionConstants.SESSION_CLEAR_TIME_TOKEN:
            return { ...state, time: undefined }
        case SessionConstants.SESSION_OPEN_MODAL:
            return { ...state, visible: true }
        case SessionConstants.SESSION_CLOSE_MODAL:
            return { ...state, visible: false }
        default:
            return state
    }
}