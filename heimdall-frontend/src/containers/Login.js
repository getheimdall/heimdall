import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { login, logout } from '../actions/auth'

import { Row, Card } from 'antd'

import logo from '../icon-heimdall.png'
import formLogo from '../form-heimdall.png'

import LoginForm from '../components/login/LoginForm'


class Login extends Component {

    componentDidMount() {
        this.props.logout()
    }

    render() {
        return (
            <Row type="flex" justify="center" className="login-row">

                <Row type="flex" justify="center" className="heimdall-logo-form">
                    <img src={logo} alt="" className="heimdall-logo" />
                </Row>
                <Card className="login-cardbox">
                    <Row type="flex" justify="center">
                        <img src={formLogo} alt="" style={{ marginBottom: 30, width: 250, height: 56 }} />
                    </Row>
                    <LoginForm submit={this.props.login} loading={this.props.loading} />
                </Card>
            </Row>
        )
    }
}

const mapStateToProps = state => {
    return {
        loading: state.auth.loading
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        login: bindActionCreators(login, dispatch),
        logout: bindActionCreators(logout, dispatch)
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Login)
