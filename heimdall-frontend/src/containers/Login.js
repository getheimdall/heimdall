import React, {Component} from 'react'
import {connect} from 'react-redux'
import {bindActionCreators} from 'redux'
import {login, logout} from '../actions/auth'

import logo from '../icon-heimdall.png'
import formLogo from '../logo-form.png'

import LoginForm from '../components/login/LoginForm'


class Login extends Component {

    componentDidMount() {
        this.props.logout()
    }

    render() {
        return (
            <div className="login-form">
                <img src={logo} alt="logo" className="logo" align="center"/>
                <br/>
                <img src={formLogo} alt="api-name" className="logo-name"/>
                <br/>
                <LoginForm submit={this.props.login} loading={this.props.loading}/>
            </div>
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
