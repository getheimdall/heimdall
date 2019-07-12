import React, { Component } from 'react'
import Particles from 'react-particles-js'

import { Layout } from 'antd'
import particlesParams from '../../utils/particles'

class LoginLayout extends Component {
    render() {
        return (
            <Layout className="login-new-layout">
                <Particles
                    params={particlesParams}
                    style={{
                        width: '100%',
                        height: '100%'
                    }}
                />
                {this.props.children}
            </Layout>
        )
    }
}

export default LoginLayout
