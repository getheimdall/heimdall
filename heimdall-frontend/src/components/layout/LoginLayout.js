import React, { Component } from 'react'
import Particles from 'react-particles-js'

import { Layout } from 'antd'
import particlesParams from '../../utils/particles'

class LoginLayout extends Component {
    render() {
        const { Content, Footer } = Layout

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
                    <Footer className="footer-bottom">
                        <a href="http://conductor.com.br">Conductor Tecnologia</a>
                    </Footer>
                </Layout>
            </Layout>
        )
    }
}

export default LoginLayout
