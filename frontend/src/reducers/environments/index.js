import { EnvironmentConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case EnvironmentConstants.GET_ENVIRONMENTS:
            return { ...state, environments: action.environments }
        case EnvironmentConstants.CLEAR_ENVIRONMENTS:
            const { environments, ...stateWithoutEnvs } = state
            return { ...stateWithoutEnvs }
        case EnvironmentConstants.GET_ENVIRONMENT:
            return { ...state, environment: action.environment }
        case EnvironmentConstants.CLEAR_ENVIRONMENT:
            const { environment, ...stateWithoutEnv } = state
            return { ...stateWithoutEnv }
        case EnvironmentConstants.ENVIRONMENT_LOADING:
            return { ...state, loading: true }
        case EnvironmentConstants.ENVIRONMENT_LOADING_FINISH:
            return { ...state, loading: false }
        case EnvironmentConstants.ENVIRONMENT_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}