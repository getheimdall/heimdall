//3rd's
import React, { Component } from 'react'
import { push } from 'connected-react-router';
import { connect } from 'react-redux'

// actions
import { getUser, initLoading, clearUser, clearUsers, update, save, remove } from '../actions/users';
import { getAllRoles } from '../actions/roles';

//components
import { Card, Row, notification } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import Loading from '../components/ui/Loading'
import UserForm from '../components/users/UserForm';

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
        const title = user ? 'Edit' : 'Add'

        return (
            <div>
                <PageHeader title="Users" icon="user" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' User'}>
                        <UserForm user={user}
                            handleDelete={this.handleDelete}
                            handleSubmit={this.handleSubmit}
                            loading={this.props.loading}
                            roles={roles ? roles.content : []}/>
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