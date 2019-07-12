import { NavbarConstants } from "../constants/actions-types";

export default (state = { keys: ['apis'] }, action) => {
    switch (action.type) {
        case NavbarConstants.UPDATE_MENU_KEYS:
            return { ...state, keys: action.keys }

        default:
            return state
    }
}