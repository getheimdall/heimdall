import React, { Component } from 'react'
import Logo from './Logo'
import { Layout, Menu, Affix } from 'antd'
import SidebarLink from './SidebarLink'

const { Sider } = Layout

class SideBar extends Component {

    constructor(props) {
        super(props)

        this.state = { collapsed: false }
    }

    onCollapse = (collapsed) => {
        this.setState({ collapsed });
    }

    render() {
        const { history } = this.props;

        return (
            <Sider collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
                <Affix>
                    <Logo history={history} collapsed={this.state.collapsed} />
                    <Menu theme="dark" mode="inline" defaultSelectedKeys={['apis']} style={!this.state.collapsed ? {width: '200px'} : {width: '80px'}}>
                        <Menu.Item key="apis" className="apis">
                            <SidebarLink to="/apis" label="APIs" history={history} icon="api" />
                        </Menu.Item>

                        <Menu.Item key="plans" className="plans">
                            <SidebarLink to="/plans" label="Plans" history={history} icon="profile" />
                        </Menu.Item>

                        <Menu.Item key="apps" className="apps">
                            <SidebarLink to="/apps" label="Apps" history={history} icon="appstore" />
                        </Menu.Item>

                        <Menu.Item key="access-tokens" className="accessToken">
                            <SidebarLink to="/tokens" label="Access Tokens" history={history} icon="key" />
                        </Menu.Item>

                        <Menu.Item key="environments" className="environments">
                            <SidebarLink to="/environments" label="Environments" history={history} icon="codepen" />
                        </Menu.Item>

                        <Menu.Item key="developers" className="developers">
                            <SidebarLink to="/developers" label="Developers" history={history} icon="code" />
                        </Menu.Item>

                        <Menu.Item key="users" className="users">
                            <SidebarLink to="/users" label="Users" history={history} icon="user" />
                        </Menu.Item>

                        <Menu.Item key="traces" className="traces">
                            <SidebarLink to="/traces" label="Traces" history={history} icon="sync" />
                        </Menu.Item>

                        <Menu.Item key="analytics" className="analytics">
                            <SidebarLink to="/analytics" label="Analytics" history={history} icon="area-chart" />
                        </Menu.Item>
                    </Menu>
                </Affix>
            </Sider>
        )
    }
}
export default SideBar