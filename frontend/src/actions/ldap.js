import {LdapConstants} from "../constants/actions-types";
import {LdapService} from "../services/LdapService";

export const initLoading = () => dispatch => {
    dispatch({ type: LdapConstants.LDAP_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: LdapConstants.LDAP_LOADING_FINISH })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: LdapConstants.LDAP_NOTIFICATION, notification})
}

export const getLdap = () => dispatch => {
    LdapService.get()
        .then(data => {
            dispatch({ type: LdapConstants.GET_LDAP, ldap: data})
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const updateLdap = (ldap) => dispatch => {
    LdapService.update(ldap)
        .then(data => {
            dispatch({ type: LdapConstants.UPDATE_LDAP, ldap: data})
            dispatch(sendNotification({ type: 'success', message: 'Ldap updated!' }))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}