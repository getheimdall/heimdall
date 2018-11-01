//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'
//components
import { Row, Form, Card, Input, Col, notification } from 'antd'
//actions
import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import ListPlans from '../components/plans/ListPlans'
import FloatButton from '../components/ui/FloatButton'
import { getAllPlans, initLoading, remove, clearPlans } from '../actions/plans'

class Plans extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllPlans())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearPlans())
    }

    handleDelete = (planId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(planId))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllPlans({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllPlans({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { plans, loading, history } = this.props
        console.log(this.props)

        if (!plans) return <Loading />

        return (
            <div>
                <PageHeader title={i18n.t('plans')} icon="profile" />

                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24} type="flex" justify="start">
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('name')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('name')} />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('description')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('description')} />)}
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListPlans dataSource={plans} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />

                    <FloatButton idButton="addPlan" history={history} to="/plans/new" label={i18n.t('add_new_plan')} />
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        plans: state.plans.plans,
        loading: state.plans.loading,
        notification: state.plans.notification
    }
}

const PlansWrapped = Form.create({})(Plans)

export default connect(mapStateToProps)(PlansWrapped)