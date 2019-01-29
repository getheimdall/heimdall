import { LdapConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case LdapConstants.LDAP_LOADING:
            return { ...state, loading: true }
        case LdapConstants.LDAP_LOADING_FINISH:
            return { ...state, loading: false }
        case LdapConstants.LDAP_NOTIFICATION:
            return { ...state, notification: action.notification}
        case LdapConstants.GET_LDAP:
            return { ...state, ldap: action.ldap}
        case LdapConstants.UPDATE_LDAP:
            return { ...state, ldap: action.ldap}
        default:
            return state
    }
}