import React from 'react'
import {Row, Col, Card, Button, Switch, Modal, notification} from 'antd'
import AceEditor from 'react-ace'
import SwaggerUI from 'swagger-ui'
import 'swagger-ui/dist/swagger-ui.css'
import {connect} from 'react-redux'
import {bindActionCreators} from "redux"

import PageHeader from "../ui/PageHeader"
import SwaggerIcon from "../icons/SwaggerIcon"
import {
    getApiById,
    updateApi,
    deleteApi,
    resetApiAction,
    getSwaggerByApi,
    clearSwaggerApi,
    updateApiWithSwagger, sendNotification, initLoading, finishLoading
} from '../../actions/apis'
import Loading from "../ui/Loading"
import {getAllEnvironments, clearEnvironments} from '../../actions/environments'

import 'brace/theme/monokai'
import 'brace/mode/json'

const confirm = Modal.confirm

class SingleApiSwaggerEditor extends React.Component {

    state = {
        swagger: "",
        apiId: 0,
        override: false
    }

    componentDidMount() {

        let apiId = this.props.match.params.id
        const {swagger} = this.state

        if (apiId) {
            this.props.getApiSwaggerById(apiId)
            this.props.getAllEnvironments()
            this.setState({...this.state, apiId: apiId})
        }

        this.updateSwaggerUI(swagger)
    }

    componentWillUpdate(nextProps, nextState) {
        const {swagger} = nextState
        this.updateSwaggerUI(swagger)
    }

    componentWillReceiveProps(nextProps) {

        const swaggerProps = this.props.swagger
        const swaggerNextProps = nextProps.swagger

        if (swaggerNextProps !== swaggerProps) {
            this.setState({...this.state, swagger: JSON.stringify(swaggerNextProps, null, '\t')})
        }

        if (nextProps.notification && nextProps.notification !== this.props.notification) {
            const { type, message, description } = nextProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.clearEnvironments()
        this.props.resetApiAction()
        this.props.clearSwagger()
    }

    updateSwaggerUI = swagger => {

        let swaggerRender = {}

        if (swagger && this.swaggerIsValid(swagger)) {
            swaggerRender = JSON.parse(swagger)
        }

        SwaggerUI({
            dom_id: '#swaggerUI',
            spec: swaggerRender
        })

        this.props.dispatch(finishLoading())
    }

    handleOnChangeSwaggerEditor = swaggerEdit => {
        if (swaggerEdit !== this.state.swagger && this.swaggerIsValid(swaggerEdit)) {
            this.props.dispatch(initLoading())
            this.setState({...this.state, swagger: JSON.stringify(JSON.parse(swaggerEdit), null, '\t') })
        }
    }

    handleOnChangeOverride = value => {

        const updateOverride = this.updateOverride

        if (value) {
            confirm({
                title: 'Confirm action',
                content: 'You will override all API resources and operations and adding those of Swagger!',
                onOk() {
                     updateOverride(true)
                },
            })
        } else {
            this.setState({ ...this.state, override: false })
        }
    }

    updateOverride = override => {
        this.setState({ ...this.state, override })
    }

    swaggerIsValid = swagger => {
        try {
            JSON.parse(swagger)
            return true
        } catch (e) {
            return false
        }
    }

    handleSaveSwagger = () => {
        const { apiId, swagger, override } = this.state
        const { updateApiWithSwagger } = this.props

        if (this.swaggerIsValid(swagger)){
            confirm({
                title: 'Confirm action',
                content: 'You are sure?',
                onOk() {
                    updateApiWithSwagger(apiId, JSON.parse(swagger), override)
                }
            })
        } else {
            this.props.dispatch(sendNotification({ type: 'error', message: 'Error', description: 'Swagger Json not valid!' }))
        }

    }

    render() {

        const { loading } = this.props
        const {swagger} = this.state

        console.log(swagger)

        const display = !loading ? 'block' : 'none'

        return (
            <Row>
                <PageHeader title="Swagger Editor" IconComponent={SwaggerIcon}/>
                <Row gutter={2}>
                    <Col sm={24} md={12}>
                        <Card title="Editor" style={{margin: '0 14px', height: 700}} extra={
                            <Row gutter={8}>
                                <Col sm={12} md={12}>
                                    <Switch checkedChildren="Override" unCheckedChildren="Override" onChange={this.handleOnChangeOverride} checked={this.state.override}/>
                                </Col>
                                <Col sm={12} md={12}>
                                    <Button size="small" onClick={this.handleSaveSwagger}>Save</Button>
                                </Col>
                            </Row>
                        }>

                            <AceEditor
                                theme="monokai"
                                mode="json"
                                onChange={this.handleOnChangeSwaggerEditor}
                                value={swagger}
                                debounceChangePeriod={2500}
                                editorProps={{$blockScrolling: 'Infinity'}}
                                width="100%" height="600px"/>

                        </Card>
                    </Col>
                    <Col sm={24} md={12}>
                        <Card title="Preview Swagger" style={{margin: '0 14px', height: 700, overflowY: 'auto'}}>
                            { loading && <Loading />}
                            <div id="swaggerUI" style={{width: '100%', height: 700, display: display}}/>
                        </Card>
                    </Col>
                </Row>
            </Row>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        api: state.apis.api,
        swagger: state.apis.swagger,
        notification: state.apis.notification,
        environments: state.environments.environments,
        loading: state.apis.loading,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getApiById: bindActionCreators(getApiById, dispatch),
        getApiSwaggerById: bindActionCreators(getSwaggerByApi, dispatch),
        updateApi: bindActionCreators(updateApi, dispatch),
        resetApiAction: bindActionCreators(resetApiAction, dispatch),
        getAllEnvironments: bindActionCreators(getAllEnvironments, dispatch),
        clearEnvironments: bindActionCreators(clearEnvironments, dispatch),
        deleteApi: bindActionCreators(deleteApi, dispatch),
        clearSwagger: bindActionCreators(clearSwaggerApi, dispatch),
        updateApiWithSwagger: bindActionCreators(updateApiWithSwagger, dispatch)
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(SingleApiSwaggerEditor)