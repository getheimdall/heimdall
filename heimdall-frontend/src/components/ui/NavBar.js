import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { withI18n } from 'react-i18next'
import { Menu, Icon, Row, Col, notification } from 'antd'

import languages from '../../constants/languages'
import { logout, getUser } from '../../actions/auth'
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"
import { clearCaches, initLoading } from '../../actions/cache'

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
        const key = e.key
        const { i18n } = this.props

        if (key.indexOf('changeLang') === 0) {
            const lgn = key.split(':')[1]
            i18n.changeLanguage(lgn)
            this.props.history.go(0)
        }

        switch (key) {
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
        const { i18n, t } = this.props
        return (
            <Row type="flex" justify="start">
                <Col sm={24} md={24}>
                    <Menu id="top-bar-menu" mode="horizontal" theme="light" style={{ lineHeight: '62px' }} onClick={this.handleClick}>
                        <SubMenu title={<span><Icon type="info-circle-o" /></span>}>
                            <MenuItemGroup title={t('heimdall_project')}>
                                {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_DELETE_CACHES]) &&
                                <Menu.Item key="heimdall:2">{t('clear_cache')}</Menu.Item>
                                }
                                {/* <Menu.Item key="heimdall:2">About</Menu.Item> */}
                                <Menu.Item key="heimdall:4">{t('license')}</Menu.Item>
                            </MenuItemGroup>
                            <MenuItemGroup title={t('developers')}>
                                {/* <Menu.Item key="setting:3">API Reference</Menu.Item> */}
                                <Menu.Item key="setting:4">{t('developer_web_page')}</Menu.Item>
                            </MenuItemGroup>
                        </SubMenu>
                        <SubMenu title={<span><Icon type="global" /></span>}>
                            {
                                languages.map(lng => {
                                    return (
                                        <Menu.Item key={`changeLang:${lng.key}`} className="heimdall-flags" disabled={lng.key === i18n.language}>
                                            <img width={32} height={32} src={lng.img} alt={lng.label}/>
                                            <label>{lng.label}</label>
                                        </Menu.Item>
                                    )
                                })
                            }
                        </SubMenu>
                        <SubMenu title={<span><Icon type="user" /> {this.props.user.username} </span>}>
                            {/* <Menu.Item key="heimdall:1">Edit profile</Menu.Item> */}
                            <Menu.Item key="logout">{t('sign_out')}</Menu.Item>
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

export default connect(mapStateToProps, mapDispatchToProps)(withI18n()(NavBar))