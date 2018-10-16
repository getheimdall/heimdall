import React, { Component } from 'react'
import {connect} from 'react-redux'

import PageHeader from "../components/ui/PageHeader"

import {Card, notification, Row} from 'antd'
import LdapForm from "../components/ldap/LdapForm"
import {getLdap, initLoading, updateLdap} from "../actions/ldap"

class SingleLdap extends Component {


    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getLdap())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(updateLdap(formObject))
    }

    render () {

        const { ldap } = this.props

        return (
            <div>
                <PageHeader title="LDAP" icon="setting"/>
                <Row className="h-row bg-white">
                    <Card style={{width: '100%'}} title="Settings">
                        <LdapForm handleSubmit={this.handleSubmit} ldap={ldap}/>
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        ldap: state.ldap.ldap,
        loading: state.ldap.loading,
        notification: state.ldap.notification
    }
}

export default connect(mapStateToProps)(SingleLdap)