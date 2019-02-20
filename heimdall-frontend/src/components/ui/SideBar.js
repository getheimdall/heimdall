import React, { Component } from 'react'
import { Layout, Menu, Affix } from 'antd'
import { connect } from 'react-redux'

import Logo from './Logo'
import i18n from "../../i18n/i18n"
import SidebarLink from './SidebarLink'
import { infoService } from "../../services/InfoService"
import { PrivilegeUtils } from "../../utils/PrivilegeUtils"
import { privileges } from "../../constants/privileges-types"
import { menuitem } from "../../constants/sidebar-items"
import { updateKeys } from '../../actions/navbar'

const { Sider } = Layout

class SideBar extends Component {

    state = {
        traces: false
    }

    componentWillMount() {
        const pathname = this.props.history.location.pathname

        if (pathname === "/") {
            this.props.dispatch(updateKeys([menuitem.DEFAULT_KEY]))
        } else {
            const key = pathname.split('/')
            this.props.dispatch(updateKeys([key[1]]))
        }

    }

    componentDidMount() {
        infoService.getManagerInfo().then(data => {
            this.setState({ ...this.state, traces: data.traces })
        })
    }

    constructor(props) {
        super(props)
        this.state = { collapsed: false }
    }

    onCollapse = (collapsed) => {
        this.setState({ collapsed });
    }

    onSelect = (item) => {
        this.props.dispatch(updateKeys(item.selectedKeys))
    }

    render() {
        const { history } = this.props;
        const { traces } = this.state

        return (
            <Sider collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse} >
                <Affix>
                    <Logo history={history} collapsed={this.state.collapsed} />
                    <Menu theme="dark" mode="inline" onSelect={this.onSelect} selectedKeys={this.props.keys} defaultSelectedKeys={['apis']} style={!this.state.collapsed ? { width: '200px' } : { width: '80px' }}>
                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_API]) &&
                            <Menu.Item key={menuitem.APIS} className="apis">
                                <SidebarLink to="/apis" label={i18n.t('apis')} history={history} icon="api" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_PLAN]) &&
                            <Menu.Item key={menuitem.PLANS} className="plans">
                                <SidebarLink to="/plans" label={i18n.t('plans')} history={history} icon="profile" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_APP]) &&
                            <Menu.Item key={menuitem.APPS} className="apps">
                                <SidebarLink to="/apps" label={i18n.t('apps')} history={history} icon="appstore" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_ACCESSTOKEN]) &&
                            <Menu.Item key={menuitem.TOKENS} className="accessToken">
                                <SidebarLink to="/tokens" label={i18n.t('access_tokens')} history={history} icon="key" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_ENVIRONMENT]) &&
                            <Menu.Item key={menuitem.ENVIRONMENTS} className="environments">
                                <SidebarLink to="/environments" label={i18n.t('environments')} history={history} icon="codepen" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_DEVELOPER]) &&
                            <Menu.Item key={menuitem.DEVELOPERS} className="developers">
                                <SidebarLink to="/developers" label={i18n.t('developers')} history={history} icon="code" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_ROLE]) &&
                            <Menu.Item key={menuitem.ROLES} className="roles">
                                <SidebarLink to="/roles" label={i18n.t('roles')} history={history} icon="solution" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_USER]) &&
                            <Menu.Item key={menuitem.USERS} className="users">
                                <SidebarLink to="/users" label={i18n.t('users')} history={history} icon="user" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_TRACES]) && traces &&
                            <Menu.Item key={menuitem.TRACES} className="traces">
                                <SidebarLink to="/traces" label={i18n.t('traces')} history={history} icon="sync" />
                            </Menu.Item>
                        }

                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_LDAP]) &&
                            <Menu.Item key={menuitem.LDAP} className="ldap">
                                <SidebarLink to="/ldap" label={i18n.t('settings_ldap')} history={history} icon="setting" />
                            </Menu.Item>
                        }
                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_PROVIDER]) &&
                            <Menu.Item key={menuitem.PROVIDERS} className="providers">
                                <SidebarLink to="/providers" label={i18n.t('providers')} history={history} icon="cluster" />
                            </Menu.Item>
                        }
                    </Menu>
                </Affix>
            </Sider>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        keys: state.navbar.keys
    }
}

export default connect(mapStateToProps)(SideBar)