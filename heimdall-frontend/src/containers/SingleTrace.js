//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'

// actions
import { getTracer, initLoading, finishLoading} from '../actions/traces';

//components
import { Card, Row, notification, Form, Col, Tag } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import ReactJson from 'react-json-view'
import Loading from '../components/ui/Loading'

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
        const title = "View trace"
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
                {console.log(trace)}
                <PageHeader title="Traces" icon="sync" />
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