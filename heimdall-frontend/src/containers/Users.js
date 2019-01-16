//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'
//components
import { Row, Input, Col, Card, Form, notification } from 'antd'
//actions
import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import ListUsers from '../components/users/ListUsers'
import RouteButton from '../components/ui/RouteButton'
import {privileges} from "../constants/privileges-types"
import ComponentAuthority from "../components/policy/ComponentAuthority"
import { getAllUsers, initLoading, remove } from '../actions/users'

class Users extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllUsers())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleDelete = (userId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(userId))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllUsers({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllUsers({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { users, loading, history } = this.props

        if (!users) return <Loading />

        return (
            <div>
                <PageHeader title={i18n.t('users')} icon="user" />
                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24} type="flex" justify="start">
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('userName')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('username')} />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('email')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('email')} />)}
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListUsers dataSource={users} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_USER]}>
                        <RouteButton idButton="addUser" history={history} to="/users/new" label={i18n.t('add_new_user')} />
                    </ComponentAuthority>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        users: state.users.users,
        loading: state.users.loading,
        notification: state.users.notification
    }
}

const UsersWrapped = Form.create({})(Users)

export default connect(mapStateToProps)(UsersWrapped)