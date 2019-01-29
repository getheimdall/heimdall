import { notification } from 'antd'

import i18n from "../../i18n/i18n"
import { resourceService } from '../../services'
import { ResourceConstants } from '../../constants/actions-types'

const getAllResourcesAction = resources => ({ type: ResourceConstants.ALL_RESOURCES, resources })

export const getAllResourcesByApi = idApi => dispatch => {
    dispatch(clearResourcesAction())
    resourceService.getResourcesByApi(idApi)
    .then(data => {
        dispatch(getAllResourcesAction(data))
    })
    .catch(error => {
        console.log(error)
    })
}

const clearResourcesAction = () => ({ type: ResourceConstants.CLEAR_RESOURCES })

export const clearResources = () => dispatch => {
    dispatch(clearResourcesAction())
}

export const toggleModal = visible => dispatch => {
    dispatch({ type: ResourceConstants.VISIBLE_MODAL, visible })
}

export const resetResource = () => dispatch => {
    dispatch({ type: ResourceConstants.RESET_RESOURCE })
}

export const initLoading = () => dispatch => {
    dispatch({ type: ResourceConstants.INIT_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: ResourceConstants.FINISH_LOADING })
}


export const getResource = (idApi, id) => dispatch => {
    resourceService.getResource(idApi, id)
    .then(data => {
        dispatch({ type: ResourceConstants.GET_RESOURCE,  resource: data })
    })
    .catch(error => {
        console.log(error)
    })
}

export const save = (idApi, resource) => dispatch => {
    dispatch(initLoading())
    resourceService.save(idApi, resource)
    .catch(error => {
        console.log(error)
        if (error.response && error.response.status === 400) {
            notification['error']({ message: i18n.t('error'), description: error.response.data.message})
        }
    })
    .then(() => {
        // console.log('finally')
        dispatch(getAllResourcesByApi(idApi))
        dispatch(toggleModal(false))
        dispatch(finishLoading())
    })
}

export const update = (idApi, resource) => dispatch => {
    dispatch(initLoading())
    resourceService.update(idApi, resource)
    .then(data => {
        dispatch(getAllResourcesByApi(idApi))
        dispatch(toggleModal(false))
        dispatch(finishLoading())
    })
    .catch(error => {
        if (error.response && error.response.status === 400) {
            notification['error']({ message: i18n.t('error'), description: error.response.data.message})
            dispatch(getAllResourcesByApi(idApi))
        }
        console.log(error)
    })
}

export const remove = (idApi, idResource) => dispatch => {
    dispatch(initLoading())
    resourceService.remove(idApi, idResource)
    .catch(error => {
        console.log(error)
        if (error.response && error.response.status === 400) {
            notification['error']({ message: i18n.t('error'), description: error.response.data.message})
        }
    })
    .then(() => {
        dispatch(getAllResourcesByApi(idApi))
        dispatch(finishLoading())
    })
}