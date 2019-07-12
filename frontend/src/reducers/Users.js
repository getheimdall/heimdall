import { UserConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case UserConstants.GET_USERS:
            return { ...state, users: action.users }
        case UserConstants.CLEAR_USERS:
            const { users, ...stateWithoutUsers } = state
            return { ...stateWithoutUsers }
        case UserConstants.GET_USER:
            return { ...state, user: action.user }
        case UserConstants.CLEAR_USER:
            const { user, ...stateWithoutUser } = state
            return { ...stateWithoutUser }
        case UserConstants.USER_LOADING:
            return { ...state, loading: true }
        case UserConstants.USER_LOADING_FINISH:
            return { ...state, loading: false }
        case UserConstants.USER_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}