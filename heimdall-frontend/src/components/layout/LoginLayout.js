import React, { Component } from 'react'
import Particles from 'react-particles-js'

import { Layout } from 'antd'
import particlesParams from '../../utils/particles'

class LoginLayout extends Component {
    render() {
        const { Content } = Layout

        return (
            <Layout className="layout login-layout">
                <Particles
                    params={particlesParams}
                    style={{
                        width: '100%',
                        height: '100%'
                    }}
                />
                <Layout>
                    <Content className="login-content">
                        {this.props.children}
                    </Content>
                </Layout>
            </Layout>
        )
    }
}

export default LoginLayout
