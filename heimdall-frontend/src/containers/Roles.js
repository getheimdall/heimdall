import React, {Component} from 'react'
import {connect} from 'react-redux'
import PageHeader from '../components/ui/PageHeader'
import {Button, Card, Col, Form, Input, notification, Row} from "antd";
import ListRoles from "../components/roles/ListRoles";
import RouteButton from "../components/ui/RouteButton";
import {initLoading, getAllRoles, remove, clearRoles, clearRole} from "../actions/roles";
import ComponentAuthority from "../components/policy/ComponentAuthority";
import {privileges} from "../constants/privileges-types";

class Roles extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllRoles())
    }

    componentWillUnmount() {
        this.props.dispatch(clearRole())
        this.props.dispatch(clearRoles())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleDelete = (roleId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(roleId))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllRoles({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllRoles({ offset: 0, limit: 10, ...payload}))
                this.setState({ ...this.state, searchQuery: payload })
            }
        })
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { roles, loading} = this.props

        return (
            <div>
                <PageHeader title='Roles' icon="solution"/>

                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24}>
                                <Col sm={24} md={5}>
                                    { getFieldDecorator('name')(<Input.Search onSearch={this.onSearchForm} placeholder="name" />)}
                                </Col>
                                <Col sm={24} md={19}>
                                    <Row type="flex" justify="end">
                                        <Button className="card-button" type="primary" icon="search" onClick={this.onSearchForm}>Search</Button>
                                    </Row>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListRoles dataSource={roles} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading}/>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_ROLE]}>
                        <RouteButton idButton="role-id" history={this.props.history} label="Add new ROLE" to="/roles/new"/>
                    </ComponentAuthority>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        roles: state.roles.roles,
        loading: state.roles.loading,
        notification: state.roles.notification
    }
}

const RolesWrapped = Form.create({})(Roles)

export default connect(mapStateToProps)(RolesWrapped)