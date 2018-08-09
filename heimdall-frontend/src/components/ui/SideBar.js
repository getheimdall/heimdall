import React, {Component} from 'react'
import Logo from './Logo'
import {Layout, Menu} from 'antd'
import SidebarLink from './SidebarLink'
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";
import {privileges} from "../../constants/privileges-types";

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
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_API]) &&
                    <Menu.Item key="apis" className="apis">
                        <SidebarLink to="/apis" label="APIs" history={history} icon="api"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_PLAN]) &&
                    <Menu.Item key="plans" className="plans">
                        <SidebarLink to="/plans" label="Plans" history={history} icon="profile"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_APP]) &&
                    <Menu.Item key="apps" className="apps">
                        <SidebarLink to="/apps" label="Apps" history={history} icon="appstore"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_ACCESSTOKEN]) &&
                    <Menu.Item key="access-tokens" className="accessToken">
                        <SidebarLink to="/tokens" label="Access Tokens" history={history} icon="key"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_ENVIRONMENT]) &&
                    <Menu.Item key="environments" className="environments">
                        <SidebarLink to="/environments" label="Environments" history={history} icon="codepen"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_DEVELOPER]) &&
                    <Menu.Item key="developers" className="developers">
                        <SidebarLink to="/developers" label="Developers" history={history} icon="code"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_USER]) &&
                    <Menu.Item key="users" className="users">
                        <SidebarLink to="/users" label="Users" history={history} icon="user"/>
                    </Menu.Item>
                    }
                    {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_TRACES]) &&
                    <Menu.Item key="traces" className="traces">
                        <SidebarLink to="/traces" label="Traces" history={history} icon="sync"/>
                    </Menu.Item>
                    }
                </Menu>
            </Sider>
        )
    }
}

export default SideBar