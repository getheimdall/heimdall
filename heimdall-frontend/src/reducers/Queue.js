import { QueueConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case QueueConstants.RECEIVE_QUEUE:
            return { ...state, count: action.count }
        case QueueConstants.PROCESS_QUEUE:
            return { ...state, count: state.count - 1 }
        case QueueConstants.ERROR_QUEUE:
            return { ...state, count: state.count - 1 }
        case QueueConstants.CLEAR_QUEUE:
            const { count, ...stateWithoutCount } = state
            return { ...stateWithoutCount }
        default:
            return state
    }
}