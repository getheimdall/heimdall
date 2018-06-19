//3rd's
import React, { Component } from 'react'
import { push } from 'connected-react-router';
import { connect } from 'react-redux'

// actions
import { getUser, initLoading, clearUser, clearUsers, update, save, remove } from '../actions/users';
import { getAllRoles } from '../actions/roles';

//components
import { Card, Row, notification, Form, Col, Button, Tag } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import ReactJson from 'react-json-view'
import Loading from '../components/ui/Loading'

//utils
import ColorUtils from '../utils/ColorUtils'


class SingleTrace extends Component {

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

    render() {
        //if (this.state.loadEntity && !user) return <Loading />
        const title = "View trace"
        const trace = {
            "method": "DELETE",
            "url": "http://localhost:8080/oauth/token",
            "resultStatus": 501,
            "durationMillis": 22,
            "insertedOnDate": "12/06/2018 03:12:40.296",
            "apiId": 1,
            "apiName": "oauth",
            "app": null,
            "accessToken": null,
            "receivedFromAddress": "",
            "clientId": null,
            "resourceId": 2,
            "appDeveloper": null,
            "operationId": 2,
            "request": null,
            "response": null,
            "level": "ASOIDJ",
            "pattern": "/token",
            "traces": [],
            "filters": [{
                "name": "HeimdallDecorationFilter",
                "timeInMillisRun": 6,
                "timeInMillisShould": 0,
                "status": "SUCCESS",
                "totalTimeInMillis": 6
            }, {
                "name": "OperationOauthPre5",
                "timeInMillisRun": 0,
                "timeInMillisShould": 0,
                "status": "SUCCESS",
                "totalTimeInMillis": 0
            }, {
                "name": "CustomSendResponseFilter",
                "timeInMillisRun": 0,
                "timeInMillisShould": 0,
                "status": "SUCCESS",
                "totalTimeInMillis": 0
            }],
            "profile": "developer"
        }

        const extraCard = (
            <Row type="flex" justify="center" align="top">
                <Col><Tag color={ColorUtils.getColorLevel(trace.level)} >{trace.level}</Tag></Col>
                <Col><Tag color={ColorUtils.getColorMethod(trace.method)}>{trace.method}</Tag></Col>
                <Col><Tag color={ColorUtils.getColorStatus(trace.resultStatus)}>{trace.resultStatus}</Tag></Col>
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
        user: state.traces.user,
        loading: state.traces.loading,
        roles: state.roles.roles,
        notification: state.traces.notification
    }
}

export default connect(mapStateToProps)(SingleTrace)