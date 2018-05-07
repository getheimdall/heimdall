import { AuthConstants } from '../../constants/actions-types'

const initialState = {
    loggedIn: false,
    errorMessage: null,
    user: {}
}

export default (state = initialState, action) => {
    switch (action.type) {
        case AuthConstants.LOGIN_SUCCESSFUL:
            return { ...state, user: action.user }
        case AuthConstants.LOGIN_FAILED:
            return { ...state, loggedIn: false, errorMessage: action.message }
        case AuthConstants.LOGOUT:
            return { ...state, loggedIn: false }
        case AuthConstants.GET_USER_LOGIN:
            return { ...state, user: action.user }
        case AuthConstants.AUTH_LOADING:
            return { ...state, loading: true }
        case AuthConstants.AUTH_LOADING_FINISH:
            return { ...state, loading: false }
        default:
            return state
    }
}