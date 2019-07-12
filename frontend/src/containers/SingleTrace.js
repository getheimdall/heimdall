//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'
import ReactJson from 'react-json-view'
//components
import { Card, Row, notification, Form, Col, Tag } from 'antd'

// actions
import i18n from "../i18n/i18n"
import { getTracer } from '../actions/traces'
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
//utils
import ColorUtils from '../utils/ColorUtils'


class SingleTrace extends Component {

    state = { loadEntity: false, timer: Date.now(), intervalSeconds: 2 }

    componentDidMount() {
        let idTrace = this.props.match.params.id
        if (idTrace) {
            this.props.dispatch(getTracer(idTrace))
            this.setState({ ...this.state, loadEntity: true })
        }
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({ message, description })
        }
    }

    render() {
        const title = i18n.t('view_trace')
        const { trace } = this.props

        if (this.state.loadEntity && !trace) return <Loading />

        let level
        let method
        let resultStatus

        if (trace) {
            level = trace.level
            method = trace.trace.method
            resultStatus = trace.trace.resultStatus
        }

        const extraCard = (
            <Row type="flex" justify="center" align="top">
                <Col><Tag color={ColorUtils.getColorLevel(level)} >{level}</Tag></Col>
                <Col><Tag color={ColorUtils.getColorMethod(method)}>{method}</Tag></Col>
                <Col><Tag color={ColorUtils.getColorStatus(resultStatus)}>{resultStatus}</Tag></Col>
            </Row>
        )

        return (
            <div>
                <PageHeader title={i18n.t('trace')} icon="sync" />
                <Row className="h-row bg-white">
                    <Card className="heimdall-card-extra"  title={title} extra={extraCard}>
                        <Form>
                            <Row gutter={24}>
                                <Col sm={24} md={24}>
                                    <ReactJson src={trace} displayDataTypes={false} name={false}/>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        trace: state.traces.trace,
        loading: state.traces.loading,
        notification: state.traces.notification
    }
}

export default connect(mapStateToProps)(SingleTrace)