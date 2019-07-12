import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, notification, Row} from "antd"

import i18n from "../i18n/i18n"
import PageHeader from "../components/ui/PageHeader"
import UserEditPasswordForm from "../components/users/UserEditPasswordForm"
import { updatePassword } from "../actions/users"

class UsersChangePassword extends Component {

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleSubmit = payload => {
        this.props.dispatch(updatePassword(payload))
    }

    render() {
        return (
            <div>
                <PageHeader title={i18n.t('change_password')} icon="lock" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }}>
                        <UserEditPasswordForm handleSubmit={this.handleSubmit}/>
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        notification: state.users.notification
    }
}


export default connect(mapStateToProps)(UsersChangePassword)