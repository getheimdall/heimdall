import {Component} from 'react'
import {userService} from "../services";
import PropTypes from "prop-types";

class ComponentAuthority extends Component {

    render() {

        const { privilegesAllowed } = this.props

        if (userService.isUserLogged() && localStorage.getItem('privileges')) {
            const rolesFromUser = localStorage.getItem('privileges')
            const roles = JSON.parse(rolesFromUser)
            const contains = roles.filter(role => privilegesAllowed.includes(role.name));
            if (contains.length === privilegesAllowed.length) {
                return this.props.children
            }
        }

        return null
    }
}

ComponentAuthority.propTypes = {
    privilegesAllowed: PropTypes.array.isRequired
}

export default ComponentAuthority