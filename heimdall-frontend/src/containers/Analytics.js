import React from 'react'
import { connect } from 'react-redux'
import {Button, Card, Col, Form, Row, InputNumber, Select, notification} from 'antd'

import Chart from "../components/ui/Chart"
import PageHeader from "../components/ui/PageHeader"
import Loading from "../components/ui/Loading"
import { getTopAccessTokens, getTopApis, getTopApps, getTopResultStatus, sendNotification} from "../actions/analytics"

class Analytics extends React.Component {

    state = {
        limit: 5,
        period: "THIS_MONTH",
        topApps: [],
        topApis: [],
        topResultStatus: [],
        topAccessTokens: []
    }

    componentDidMount() {

        const { limit, period } = this.state
        this.getCharts(limit, period)
    }

    componentWillUpdate(nextProps, nextState) {
        const { limit, period } = this.state

        if (nextState.limit !== limit || nextState.period !== period) {
            this.getCharts(nextState.limit, nextState.period)
        }
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.updateLimitAndPeriod(payload.limit, payload.period)
            }
        });
    }

    updateLimitAndPeriod = (limit, period) => {
        this.setState({...this.state, limit: limit, period: period })
    }

    getCharts = (limit, period) => {
            this.props.dispatch(getTopApps(limit, period))
            this.props.dispatch(getTopApis(limit, period))
            this.props.dispatch(getTopAccessTokens(limit, period))
            this.props.dispatch(getTopResultStatus(limit, period))
            this.props.dispatch(sendNotification({ type: 'success', message: 'Metrics update!' }))
    }

    render() {
        const { topApps, topResultStatus, topAccessTokens, topApis, loading } = this.props

        const {getFieldDecorator} = this.props.form

        if (loading) {
            return <Loading />
        }

        return (
            <div>
                <PageHeader title="Analytics" icon="area-chart"/>

                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={10}>
                                <Col sm={2} md={2}>
                                    {getFieldDecorator('limit', {
                                        initialValue: this.state.limit
                                    })(<InputNumber placeholder="Limit" style={{width: '100%'}}/>)}
                                </Col>
                                <Col sm={22} md={4}>
                                    {getFieldDecorator('period', {
                                        initialValue: this.state.period
                                    })(
                                        <Select optionFilterProp="children" placeholder="Please select a period">
                                            <Select.Option key="TODAY">TODAY</Select.Option>
                                            <Select.Option key="YESTERDAY">YESTERDAY</Select.Option>
                                            <Select.Option key="THIS_WEEK">THIS WEEK</Select.Option>
                                            <Select.Option key="LAST_WEEK">LAST WEEK</Select.Option>
                                            <Select.Option key="THIS_MONTH">THIS MONTH</Select.Option>
                                            <Select.Option key="LAST_MONTH">LAST MONTH</Select.Option>
                                        </Select>
                                    )}
                                </Col>
                                <Col sm={24} md={5}>
                                    <Button type="primary" onClick={this.onSearchForm}>Apply filter</Button>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row>
                    <Card>
                        <Row gutter={24}>
                            <Col sm={24} md={12}>
                                <Chart metrics={topApps} title="Top Apps" color="#5183F7"/>
                            </Col>
                            <Col sm={24} md={12}>
                                <Chart metrics={topApis} title="Top APIs" color="#D9535A"/>
                            </Col>
                            <Col sm={24} md={12}>
                                <Chart metrics={topAccessTokens} title="Top Access Tokens" color="#2F4554"/>
                            </Col>
                            <Col sm={24} md={12}>
                                <Chart metrics={topResultStatus} title="Top Result Status" color="#C3BFD9"/>
                            </Col>
                        </Row>
                    </Card>
                </Row>
            </div>
        )
    }
}

const AnalyticsWrapped = Form.create({})(Analytics)

const mapStateToProps = state => {
    return {
        topApps: state.analytics.topApps,
        topApis: state.analytics.topApis,
        topAccessTokens: state.analytics.topAccessTokens,
        topResultStatus: state.analytics.topResultStatus,
        loading: state.analytics.loading,
        notification: state.analytics.notification
    }
}

export default connect(mapStateToProps)(AnalyticsWrapped)