import React from 'react'
import {Row, Col, Card, Button} from 'antd'
import AceEditor from 'react-ace'
import SwaggerUI from 'swagger-ui'
import 'swagger-ui/dist/swagger-ui.css'
import {connect} from 'react-redux'
import {bindActionCreators} from "redux"

import Loading from "../ui/Loading"
import PageHeader from "../ui/PageHeader"
import SwaggerIcon from "../icons/SwaggerIcon"
import {getApiById, updateApi, deleteApi, resetApiAction} from '../../actions/apis'
import {getAllEnvironments, clearEnvironments} from '../../actions/environments'

import 'brace/theme/monokai'
import 'brace/mode/json'

class SingleApiSwaggerEditor extends React.Component {

    state = {
        swagger: {
            "swagger": "2.0",
            "info": {
                "description": "API Gateway for managing APIs",
                "version": "1",
                "title": "Heimdall API Gateway"
            },
            "host": "localhost:9090",
            "basePath": "/",
            "tags": [],
            "paths": {},
            "definitions": {}
        }
    }

    componentDidMount() {

        let idApi = this.props.match.params.id
        if (idApi) {
            this.props.getApiById(idApi)
            this.props.getAllEnvironments()
        }

        const {swagger} = this.state

        const swaggerByApi = this.changeValuesByApi(swagger)
        this.updateSwaggerUI(swaggerByApi)

    }

    componentWillUpdate(nextProps, nextState) {
        if (nextState.swagger !== this.state.swagger) {
            const {swagger} = nextState
            const swaggerByApi = this.changeValuesByApi(swagger)
            this.updateSwaggerUI(swaggerByApi)
        }
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.api !== this.props.api) {
            const { swagger } = this.state
            const swaggerByApi = this.changeValuesByApi(swagger, nextProps)
            this.setState({ ...this.state, swagger: swaggerByApi })
        }
    }

    componentWillUnmount() {
        this.props.clearEnvironments()
        this.props.resetApiAction()
    }

    updateSwaggerUI = swagger => {
        if (swagger) {
            SwaggerUI({
                dom_id: '#swaggerUI',
                spec: swagger
            })
        }
    }

    changeValuesByApi = (swagger, nextProps)=> {

        let api = {}

        if (nextProps && nextProps.api) {
            api = nextProps.api
        } else {
            api = this.props.api
        }

        let swaggerWrapped = JSON.parse(JSON.stringify(swagger))

        if (api) {
            if (!swaggerWrapped.info) {
                swaggerWrapped.info = {}
            }
            swaggerWrapped.info.description = api.description
            swaggerWrapped.info.title = api.name
            swaggerWrapped.info.version = api.version
            swaggerWrapped.host = api.environments[0].inboundURL
            swaggerWrapped.basePath = api.basePath
        }
        return swaggerWrapped
    }

    handleOnChangeSwaggerEditor = swaggerEdit => {
        try {
            this.setState({...this.state, swagger: JSON.parse(swaggerEdit)})
        } catch (e) {
        }
    }

    render() {
        const {api} = this.props
        const {swagger} = this.state
        let swaggerToEdit = ''
        if (swagger) {
            swaggerToEdit = JSON.stringify(this.changeValuesByApi(swagger), null, '\t')
        }

        const display = api ? 'block' : 'none'

        return (
            <Row>
                <PageHeader title="Swagger Editor" IconComponent={SwaggerIcon}/>
                <Row gutter={2}>
                    <Col sm={24} md={12}>
                        <Card title="Editor" style={{margin: '0 14px', height: 700}} extra={
                            <Button size="small">Save</Button>
                        }>
                            {
                                !api && <Loading/>
                            }
                            {
                                api &&
                                (
                                    <AceEditor
                                        theme="monokai"
                                        mode="json"
                                        onChange={this.handleOnChangeSwaggerEditor}
                                        value={swaggerToEdit}
                                        debounceChangePeriod={1500}
                                        editorProps={{$blockScrolling: 'Infinity'}}
                                        width="100%" height="600px"/>
                                )
                            }
                        </Card>
                    </Col>
                    <Col sm={24} md={12}>
                        <Card title="Preview Swagger" style={{margin: '0 14px', height: 700, overflowY: 'auto'}}>
                            { !api && <Loading />}
                            <div id="swaggerUI" style={{width: '100%', height: 700, display: display }}/>
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
        environments: state.environments.environments
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getApiById: bindActionCreators(getApiById, dispatch),
        updateApi: bindActionCreators(updateApi, dispatch),
        resetApiAction: bindActionCreators(resetApiAction, dispatch),
        getAllEnvironments: bindActionCreators(getAllEnvironments, dispatch),
        clearEnvironments: bindActionCreators(clearEnvironments, dispatch),
        deleteApi: bindActionCreators(deleteApi, dispatch),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(SingleApiSwaggerEditor)