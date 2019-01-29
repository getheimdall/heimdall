import {Component} from 'react'
import PropTypes from "prop-types";
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";

class ComponentAuthority extends Component {

    render() {

        const { privilegesAllowed } = this.props
        if (PrivilegeUtils.verifyPrivileges(privilegesAllowed)){
            return this.props.children
        }

        return null
    }
}

ComponentAuthority.propTypes = {
    children: PropTypes.oneOfType([
        PropTypes.element,
        PropTypes.arrayOf(PropTypes.element)
    ]).isRequired,
    privilegesAllowed: PropTypes.array.isRequired
}

export default ComponentAuthority