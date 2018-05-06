import { OperationConstants } from '../../constants/actions-types'

const initialState = {
    list: []
}

export default (state = initialState, action) => {
    switch (action.type) {
        case OperationConstants.OPERATIONS_FROM_RESOURCES:
            return { ...state, operations: action.operations }
        case OperationConstants.CLEAR_OPERATIONS:
            const { operations, ...stateWithoutOperations } = state
            return { ...stateWithoutOperations }
        case OperationConstants.INIT_LOADING:
            return { ...state, loading: true }
        case OperationConstants.FINISH_LOADING:
            return { ...state, loading: false }
        case OperationConstants.VISIBLE_MODAL_OPERATION:
            return { ...state, visibleModal: action.visible }
        case OperationConstants.RESET_OPERATION:
            const { operation, ...stateWithoutOperation } = state
            return { ...stateWithoutOperation }
        case OperationConstants.GET_OPERATION:
            return { ...state, operation: action.operation }
        default:
            return state
    }
}