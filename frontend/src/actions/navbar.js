import { NavbarConstants } from '../constants/actions-types'

export const updateKeys = keys => dispatch => {
    dispatch({ type: NavbarConstants.UPDATE_MENU_KEYS, keys })
}