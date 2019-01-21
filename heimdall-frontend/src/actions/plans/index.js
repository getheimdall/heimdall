import { push } from 'connected-react-router'

import i18n from "../../i18n/i18n"
import { PlanConstants } from '../../constants/actions-types'
import { planService } from '../../services'

export const initLoading = () => dispatch => {
    dispatch({ type: PlanConstants.PLAN_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: PlanConstants.PLAN_LOADING_FINISH })
}

export const getAllPlans = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = { params: query }
    planService.getPlans(parameters)
        .then(data => {
            dispatch({ type: PlanConstants.GET_PLANS, plans: data })
            dispatch(finishLoading())
        })
}

export const clearPlans = () => dispatch => {
    dispatch({ type: PlanConstants.CLEAR_PLANS })
}

export const getPlan = planId => dispatch => {
    planService.getPlan(planId)
        .then(data => dispatch({ type: PlanConstants.GET_PLAN, plan: data }))
        .catch(error => console.log(error))
}

export const clearPlan = () => dispatch => {
    dispatch({ type: PlanConstants.CLEAR_PLAN })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: PlanConstants.PLAN_NOTIFICATION, notification })
}

export const save = plan => dispatch => {
    planService.save(plan)
        .then(data => {
            dispatch(sendNotification({ type: 'success', message:  i18n.t('plan_saved') }))
            dispatch(push('/plans'))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const update = plan => dispatch => {
    planService.update(plan)
        .then(data => {
            dispatch(getPlan(plan.id))
            dispatch(sendNotification({ type: 'success', message: i18n.t('plan_updated') }))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(getPlan(plan.id))
            dispatch(finishLoading())
        })
}

export const remove = planId => dispatch => {
    planService.remove(planId)
        .then(data => {
            dispatch(getAllPlans())
            dispatch(sendNotification({ type: 'success', message: i18n.t('plan_removed') }))
        })
}

