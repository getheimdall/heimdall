import {TraceConstants} from "../constants/actions-types";

export default (state = {}, action) => {
    switch (action.type) {
        case TraceConstants.TRACE_LOADING:
            return { ...state, loading: true}
        case TraceConstants.TRACE_LOADING_FINISH:
            return { ...state, loading: false}
        case TraceConstants.TRACE_NOTIFICATION:
            return { ...state, notification: action.notification}
        case TraceConstants.GET_TRACE:
            return { ...state, trace: action.trace}
        case TraceConstants.GET_TRACES:
            return { ...state, traces: action.traces}
        default:
            return state
    }
}