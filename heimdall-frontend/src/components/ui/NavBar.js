import React, { Component } from 'react'
import { Menu, Icon, Row, Col, Checkbox } from 'antd'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { logout, getUser } from '../../actions/auth'

const SubMenu = Menu.SubMenu
const MenuItemGroup = Menu.ItemGroup

class NavBar extends Component {

    componentDidMount() {
        this.props.getUser()
    }

    handleClick = (e) => {
        switch (e.key) {
            case 'logout':
                this.props.logout()
                break;
            case 'heimdall:1':
                this.props.joyride.reset(true)
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
                <Col sm={2} md={2}>
                    <Checkbox defaultChecked onChange={this.handleTour}>Keep Tour</Checkbox>
                </Col>
                <Col sm={22} md={22}>
                    <Menu id="top-bar-menu" mode="horizontal" theme="light" style={{ lineHeight: '62px' }} onClick={this.handleClick}>
                        <SubMenu title={<span><Icon type="info-circle-o" /></span>}>
                            <MenuItemGroup title="Heimdall Project">
                                <Menu.Item key="heimdall:1">Reset Tour</Menu.Item>
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
        user: state.auth.user
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        logout: bindActionCreators(logout, dispatch),
        getUser: bindActionCreators(getUser, dispatch)
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(NavBar)