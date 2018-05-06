import { LOGIN_FAILED, LOGOUT, GET_USER_LOGIN, LOGIN_SUCCESSFUL } from '../../constants/actions-types'

const initialState = {
    loggedIn: false,
    errorMessage: null,
    user: {}
}

export default (state = initialState, action) => {
    switch (action.type) {
        case LOGIN_SUCCESSFUL:
            return { ...state, user: action.user }
        case LOGIN_FAILED:
            return { ...state, loggedIn: false, errorMessage: action.message }
        case LOGOUT:
            return { ...state, loggedIn: false }
        case GET_USER_LOGIN:
            return { ...state, user: action.user }
        default:
            return state
    }
}