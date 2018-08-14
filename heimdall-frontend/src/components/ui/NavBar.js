import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Menu, Icon, Row, Col, notification } from 'antd'
import { logout, getUser } from '../../actions/auth'
import { clearCaches, initLoading } from '../../actions/cache'
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";
import {privileges} from "../../constants/privileges-types";

const SubMenu = Menu.SubMenu
const MenuItemGroup = Menu.ItemGroup

class NavBar extends Component {

    componentDidMount() {
        this.props.getUser()
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleClick = (e) => {
        switch (e.key) {
            case 'logout':
                this.props.logout()
                break;
            case 'heimdall:2':
                this.props.initLoading()
                this.props.clearCaches()
                break;
            default:
                break;
        }
    }

    handleTour = (e) => {
        this.props.handleTour(e.target.checked)
    }

    render() {
        return (
            <Row type="flex" justify="start">
                <Col sm={24} md={24}>
                    <Menu id="top-bar-menu" mode="horizontal" theme="light" style={{ lineHeight: '62px' }} onClick={this.handleClick}>
                        <SubMenu title={<span><Icon type="info-circle-o" /></span>}>
                            <MenuItemGroup title="Heimdall Project">
                                {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_DELETE_CACHES]) &&
                                <Menu.Item key="heimdall:2">Clear Cache</Menu.Item>
                                }
                                {/* <Menu.Item key="heimdall:2">About</Menu.Item> */}
                                <Menu.Item key="heimdall:4">License</Menu.Item>
                            </MenuItemGroup>
                            <MenuItemGroup title="Developers">
                                {/* <Menu.Item key="setting:3">API Reference</Menu.Item> */}
                                <Menu.Item key="setting:4">Developer webpage</Menu.Item>
                            </MenuItemGroup>
                        </SubMenu>

                        <SubMenu title={<span><Icon type="user" /> {this.props.user.username} </span>}>
                            {/* <Menu.Item key="heimdall:1">Edit profile</Menu.Item> */}
                            <Menu.Item key="logout">Logout</Menu.Item>
                        </SubMenu>
                    </Menu>
                </Col>
            </Row>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        user: state.auth.user,
        notification: state.caches.notification
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        logout: bindActionCreators(logout, dispatch),
        getUser: bindActionCreators(getUser, dispatch),
        clearCaches: bindActionCreators(clearCaches, dispatch),
        initLoading: bindActionCreators(initLoading, dispatch)
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(NavBar)