import { QueueConstants } from '../constants/actions-types'

export const receiveQueue = (total) => dispatch => {
    dispatch({ type: QueueConstants.RECEIVE_QUEUE, count: total })
}

// export const processQueue = (total) => dispatch => {
//     dispatch({ type: QueueConstants.PROCESS_QUEUE })
// }

export const clearQueue = () => dispatch => {
    dispatch({ type: QueueConstants.CLEAR_QUEUE })
}