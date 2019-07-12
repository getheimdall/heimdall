import { SessionConstants } from '../constants/actions-types'

export const openModalSession = () => dispatch => {
    dispatch({ type: SessionConstants.SESSION_OPEN_MODAL })
}

export const closeModalSession = () => dispatch => {
    dispatch({ type: SessionConstants.SESSION_CLOSE_MODAL })
}

export const updateTime = time => dispatch => {
    dispatch({ type: SessionConstants.SESSION_RENEW_TIME, time: time })
}

export const clearTime = () => dispatch => {
    dispatch({ type: SessionConstants.SESSION_CLEAR_TIME_TOKEN })
}