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
        //if (this.state.loadEntity && !trace) return <Loading />
        const title = "View trace"
        const trace = {
            "id": {
                "timestamp": 1529070668,
                "machineIdentifier": 11183442,
                "processIdentifier": 11432,
                "counter": 11228599,
                "time": 1529070668000,
                "date": "2018-06-15T13:51:08.000+0000",
                "timeSecond": 1529070668
            },
            "trace": {
                "method": "GET",
                "url": "http://localhost:8080/basepath/test",
                "resultStatus": 200,
                "durationMillis": 136,
                "insertedOnDate": "15/06/2018 10:51:08.120",
                "apiId": 1,
                "apiName": "Simple API",
                "app": null,
                "accessToken": null,
                "receivedFromAddress": "",
                "clientId": null,
                "resourceId": 1,
                "appDeveloper": null,
                "operationId": 1,
                "request": null,
                "response": null,
                "pattern": "/test",
                "traces": [
                    {
                        "description": "Localized mock interceptor",
                        "insertedOnDate": "15/06/2018 10:51:08.136",
                        "content": "\"{\\\"name\\\":\\\"Mock Example\\\"}\""
                    }
                ],
                "filters": [
                    {
                        "name": "HeimdallDecorationFilter",
                        "timeInMillisRun": 6,
                        "timeInMillisShould": 0,
                        "status": "SUCCESS",
                        "totalTimeInMillis": 6
                    },
                    {
                        "name": "OperationMockPre1",
                        "timeInMillisRun": 1,
                        "timeInMillisShould": 1,
                        "status": "SUCCESS",
                        "totalTimeInMillis": 2
                    },
                    {
                        "name": "CustomSendResponseFilter",
                        "timeInMillisRun": 4,
                        "timeInMillisShould": 0,
                        "status": "SUCCESS",
                        "totalTimeInMillis": 4
                    }
                ],
                "profile": "developer"
            },
            "logger": "mongo",
            "level": "INFO",
            "thread": "http-nio-8080-exec-5",
            "ts": "2018-06-15T13:51:08.274+0000"
        }

        const extraCard = (
            <Row type="flex" justify="center" align="top">
                <Col><Tag color={ColorUtils.getColorLevel(trace.level)} >{trace.level}</Tag></Col>
                <Col><Tag color={ColorUtils.getColorMethod(trace.trace.method)}>{trace.trace.method}</Tag></Col>
                <Col><Tag color={ColorUtils.getColorStatus(trace.trace.resultStatus)}>{trace.trace.resultStatus}</Tag></Col>
            </Row>
        )

        return (
            <div>
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