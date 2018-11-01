//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'
import { push } from 'connected-react-router'
//components
import { Card, Row, notification } from 'antd'
// actions
import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import { getAllRoles } from '../actions/roles'
import PageHeader from '../components/ui/PageHeader'
import UserForm from '../components/users/UserForm'
import { getUser, initLoading, clearUser, clearUsers, update, save, remove } from '../actions/users'

class SingleUser extends Component {

    state = { loadEntity: false, timer: Date.now(), intervalSeconds: 2 }

    componentDidMount() {
        let idUser = this.props.match.params.id
        if (idUser) {
            this.props.dispatch(getUser(idUser))
            this.setState({ ...this.state, loadEntity: true })
        }
        this.props.dispatch(getAllRoles())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearUser())
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        if (formObject.id) {
            this.props.dispatch(clearUser())
            this.props.dispatch(update(formObject))
        } else {
            this.props.dispatch(save(formObject))
        }
    }

    handleDelete = (userId) => {
        this.props.dispatch(remove(userId))
        this.props.dispatch(clearUsers())
        this.props.dispatch(push('/users'))
    }

    render() {
        const { user, roles } = this.props

        if (this.state.loadEntity && !user) return <Loading />
        const title = user ? i18n.t('edit') : i18n.t('add')

        return (
            <div>
                <PageHeader title={i18n.t('users')} icon="user" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' ' + i18n.t('user')}>
                        <UserForm user={user}
                            handleDelete={this.handleDelete}
                            handleSubmit={this.handleSubmit}
                            loading={this.props.loading}
                            roles={roles}/>
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        user: state.users.user,
        loading: state.users.loading,
        roles: state.roles.roles,
        notification: state.users.notification
    }
}

export default connect(mapStateToProps)(SingleUser)