import { ScopeConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case ScopeConstants.GET_SCOPES:
            return { ...state, scopes: action.scopes }
        case ScopeConstants.CLEAR_SCOPES:
            const { scopes, ...stateWithoutScopes } = state
            return { ...stateWithoutScopes }
        case ScopeConstants.GET_SCOPE:
            return { ...state, scope: action.scope }
        case ScopeConstants.CLEAR_SCOPE:
            const { scope, ...stateWithoutScope } = state
            return { ...stateWithoutScope }
        case ScopeConstants.SCOPE_LOADING:
            return { ...state, loading: true }
        case ScopeConstants.SCOPE_LOADING_FINISH:
            return { ...state, loading: false }
        case ScopeConstants.FORM_LOADING:
            return { ...state, formLoading: action.loading }
        case ScopeConstants.SCOPE_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}