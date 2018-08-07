import React, {Component} from 'react'
import Logo from './Logo'
import {Layout, Menu} from 'antd'
import SidebarLink from './SidebarLink'
import ComponentAuthority from "../ComponentAuthority";
import {
    PRIVILEGE_CREATE_ACCESSTOKEN,
    PRIVILEGE_CREATE_API,
    PRIVILEGE_CREATE_APP, PRIVILEGE_CREATE_DEVELOPER, PRIVILEGE_CREATE_ENVIRONMENT,
    PRIVILEGE_CREATE_PLAN, PRIVILEGE_CREATE_TRACES, PRIVILEGE_CREATE_USER
} from "../../utils/ConstantsPrivileges";

const {Sider} = Layout

class SideBar extends Component {

    constructor(props) {
        super(props)

        this.state = {collapsed: true}
    }

    onCollapse = (collapsed) => {
        this.setState({collapsed});
    }

    render() {
        const {history} = this.props;

        return (
            <Sider collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
                <Logo history={history} collapsed={this.state.collapsed}/>
                <Menu theme="dark" mode="inline" defaultSelectedKeys={['apis']}>
                    <Menu.Item key="apis" className="apis">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_API]}>
                            <SidebarLink to="/apis" label="APIs" history={history} icon="api"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="plans" className="plans">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_PLAN]}>
                            <SidebarLink to="/plans" label="Plans" history={history} icon="profile"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="apps" className="apps">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_APP]}>
                            <SidebarLink to="/apps" label="Apps" history={history} icon="appstore"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="access-tokens" className="accessToken">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_ACCESSTOKEN]}>
                            <SidebarLink to="/tokens" label="Access Tokens" history={history} icon="key"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="environments" className="environments">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_ENVIRONMENT]}>
                            <SidebarLink to="/environments" label="Environments" history={history} icon="codepen"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="developers" className="developers">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_DEVELOPER]}>
                            <SidebarLink to="/developers" label="Developers" history={history} icon="code"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="users" className="users">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_USER]}>
                            <SidebarLink to="/users" label="Users" history={history} icon="user"/>
                        </ComponentAuthority>
                    </Menu.Item>

                    <Menu.Item key="traces" className="traces">
                        <ComponentAuthority privilegesAllowed={[PRIVILEGE_CREATE_TRACES]}>
                            <SidebarLink to="/traces" label="Traces" history={history} icon="sync"/>
                        </ComponentAuthority>
                    </Menu.Item>
                </Menu>
            </Sider>
        )
    }
}

export default SideBar