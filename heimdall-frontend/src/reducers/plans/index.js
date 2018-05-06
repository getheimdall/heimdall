import { PlanConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case PlanConstants.GET_PLANS:
            return { ...state, plans: action.plans }
        case PlanConstants.CLEAR_PLANS:
            const { plans, ...stateWithoutPlans } = state
            return { ...stateWithoutPlans }
        case PlanConstants.GET_PLAN:
            return { ...state, plan: action.plan }
        case PlanConstants.CLEAR_PLAN:
            const { plan, ...stateWithoutPlan } = state
            return { ...stateWithoutPlan }
        case PlanConstants.PLAN_LOADING:
            return { ...state, loading: true }
        case PlanConstants.PLAN_LOADING_FINISH:
            return { ...state, loading: false }
        case PlanConstants.PLAN_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}