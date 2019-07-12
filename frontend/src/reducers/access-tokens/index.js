import { AccessTokenConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case AccessTokenConstants.GET_ACCESS_TOKENS:
            return { ...state, accessTokens: action.accessTokens }
        case AccessTokenConstants.CLEAR_ACCESS_TOKENS:
            const { accessTokens, ...stateWithoutTokens } = state
            return { ...stateWithoutTokens }
        case AccessTokenConstants.GET_ACCESS_TOKEN:
            return { ...state, accessToken: action.accessToken }
        case AccessTokenConstants.CLEAR_ACCESS_TOKEN:
            const { accessToken, ...stateWithoutToken } = state
            return { ...stateWithoutToken }
        case AccessTokenConstants.ACCESS_TOKEN_LOADING:
            return { ...state, loading: true }
        case AccessTokenConstants.ACCESS_TOKEN_LOADING_FINISH:
            return { ...state, loading: false }
        case AccessTokenConstants.ACCESS_TOKEN_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}