import { OperationConstants } from '../../constants/actions-types'
import { operationService } from '../../services'

export const toggleModal = visible => dispatch => {
    dispatch({ type: OperationConstants.VISIBLE_MODAL_OPERATION, visible })
}

export const resetOperation = () => dispatch => {
    dispatch({ type: OperationConstants.RESET_OPERATION })
}

export const getOperation = (idApi, idResource, idOperation) => dispatch => {
    operationService.getOperation(idApi, idResource, idOperation)
        .then(data => {
            dispatch({ type: OperationConstants.GET_OPERATION, operation: data })
        })
        .catch(error => {
            console.log(error)
        })
}

export const getAllOperations = (apiId, resourceId) => dispatch => {
    operationService.getOperationsByResource(apiId, resourceId)
        .then(data => {
            dispatch({ type: OperationConstants.OPERATIONS_FROM_RESOURCES, operations: data })
        })
}

export const clearOperations = () => dispatch => {
    dispatch({ type: OperationConstants.CLEAR_OPERATIONS })
}