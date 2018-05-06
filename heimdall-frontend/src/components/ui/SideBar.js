import React, { Component } from 'react'
import Logo from './Logo'
import { Layout, Menu } from 'antd'
import SidebarLink from './SidebarLink'

const { Sider } = Layout

class SideBar extends Component {

    constructor(props) {
        super(props)

        this.state = { collapsed: true }
    }

    componentDidMount() {
        this.props.addSteps([{
            title: 'Environments',
            text: 'Here you can manage an Environment. To add an <b>API</b> you require an Environment created.',
            selector: '.environments',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }, {
            title: 'Apis',
            text: `Here you can manage the <i>API's</i> created by you.`,
            selector: '.apis',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }, {
            title: 'Plans',
            text: `Plan is a tag to attach to an API, you can't attach nothing direct to an API, to attach something to an API then you need create a Plan and attach something to this plan.`,
            selector: '.plans',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }, {
            title: 'Users',
            text: `Here you can manage users and their permissions too.`,
            selector: '.users',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }, {
            title: 'Developers',
            text: `You can manage developers that use the api's registered in Heimdall. Developers can create <i>APPS</i>`,
            selector: '.developers',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }, {
            title: 'Apps',
            text: `Apps are systems created by Developers. Apps are used to provide access to their api's, every app created will have a client id attached`,
            selector: '.apps',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }, {
            title: 'Access Token',
            text: `Here you can manage Access Tokens to provide some authorization when APP will consume some API in the Heimdall, to this work it's necessary an Access Token Interceptor attached `,
            selector: '.accessToken',
            position: 'right',
            type: 'hover',
            style: {
                beacon: {
                    inner: '#FFA613',
                    outer: '#FFA613'
                }
            }
        }])
    }

    onCollapse = (collapsed) => {
        this.setState({ collapsed });
    }

    render() {
        const { history } = this.props;

        return (
            <Sider collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse} >
                <Logo history={history} collapsed={this.state.collapsed} />
                <Menu theme="dark" mode="inline" defaultSelectedKeys={['apis']}>
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

                    <Menu.Item key="Tracer" disabled>
                        <SidebarLink to="/" label="Tracer" history={history} icon="sync" />
                    </Menu.Item>
                </Menu>
            </Sider>
        )
    }
}
export default SideBar