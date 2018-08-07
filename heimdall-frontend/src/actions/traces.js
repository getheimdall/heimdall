import {TraceConstants} from '../constants/actions-types'
import {traceService} from '../services'

export const initLoading = () => dispatch => {
    dispatch({type: TraceConstants.TRACE_LOADING})
}

export const finishLoading = () => dispatch => {
    dispatch({type: TraceConstants.TRACE_LOADING_FINISH})
}

export const getAllTraces = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = {params: query}
    traceService.getTraces(parameters)
        .then(data => {
            if (data.content) {
                data.content = data.content.filter((objectTrace) => objectTrace.trace !== null)
            }
            dispatch({type: TraceConstants.GET_TRACES, traces: data})
            dispatch(finishLoading())
        })
}

export const getTracer = tracerId => dispatch => {
    traceService.getTrace(tracerId)
        .then(data => dispatch({type: TraceConstants.GET_TRACE, trace: data}))
        .catch(error => console.log(error))
}

export const sendNotification = notification => dispatch => {
    dispatch({type: TraceConstants.TRACE_LOADING_FINISH, notification})
}
