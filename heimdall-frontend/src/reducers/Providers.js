import { ProviderConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case ProviderConstants.GET_PROVIDERS:
            return { ...state, providers: action.providers }
        case ProviderConstants.CLEAR_PROVIDERS:
            const { providers, ...stateWithoutProviders } = state
            return { ...stateWithoutProviders }
        case ProviderConstants.GET_PROVIDER:
            return { ...state, provider: action.provider }
        case ProviderConstants.CLEAR_PROVIDER:
            const { provider, ...stateWithoutProvider } = state
            return { ...stateWithoutProvider }
        case ProviderConstants.PROVIDER_LOADING:
            return { ...state, loading: true }
        case ProviderConstants.PROVIDER_LOADING_FINISH:
            return { ...state, loading: false }
        case ProviderConstants.PROVIDER_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}