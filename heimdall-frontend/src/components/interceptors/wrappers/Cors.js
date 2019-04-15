import React from 'react'
import PropType from 'prop-types'
import { Form, Input, Col, Button } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item

class Cors extends React.Component {

    state = {
        cors: {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Credentials': true,
            'Access-Control-Allow-Methods': 'POST, GET, PUT, PATCH, DELETE, OPTIONS',
            'Access-Control-Allow-Headers': 'origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential',
            'Access-Control-Max-Age': 3600
        },
        paramName: '',
        paramValue: ''
    }

    handleUpdateValue = (name, value) => {
        console.log('HANDLE UPDATE VALUE TO PARAM = ', name)
        let { cors } = this.state
        cors[name] = value
        this.setState({ ...this.state, cors: cors })
    }

    handleRemoveParam = name => {
        console.log('HANDLE REMOVE PARAM')
        let { cors } = this.state
        delete cors[name]
        this.setState({ ...this.state, cors: cors })
    }

    handleUpdateParamName = event => {
        this.setState({ ...this.state, paramName: event.target.value })
    }

    handleUpdateParamValue = event => {
        this.setState({ ...this.state, paramValue: event.target.value })
    }

    addParam = () => {
        console.log('ADD PARAM')
        let { cors, paramName, paramValue } = this.state

        if (paramName && paramValue) {
            cors[paramName] = paramValue
            this.setState({ ...this.state, cors: cors, paramName: '', paramValue: '' })
        }
    }

    render() {

        const { getFieldDecorator } = this.props.form
        const { cors } = this.state

        const keysCors = Object.keys(cors)

        return(
            <React.Fragment>
                {
                    getFieldDecorator('content', { initialValue: this.state.cors })(<Input type="hidden"/>)
                }
                {
                    keysCors.map(key => {
                        return (
                            <React.Fragment key={key}>
                                <Col sm={4} md={11}>
                                    <FormItem label={i18n.t('param_name')}>
                                        <Input type="text" value={key} disabled/>
                                    </FormItem>
                                </Col>
                                <Col sm={4} md={11}>
                                    <FormItem label={i18n.t('param_value')}>
                                        <Input type="text" value={cors[key]} onChange={(value) => this.handleUpdateValue(key, value)} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>
                                    </FormItem>
                                </Col>
                                <Col sm={4} md={2}>
                                    <FormItem label=" " colon={false}>
                                        <Button icon="delete" onClick={() => this.handleRemoveParam(key)} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>
                                    </FormItem>
                                </Col>
                            </React.Fragment>
                        )
                    })
                }
                <Col sm={4} md={11}>
                    <FormItem label={i18n.t('param_name')}>
                        <Input type="text" value={this.state.paramName} onChange={this.handleUpdateParamName} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>
                    </FormItem>
                </Col>
                <Col sm={4} md={11}>
                    <FormItem label={i18n.t('param_value')}>
                        <Input type="text" value={this.state.paramValue} onChange={this.handleUpdateParamValue} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>
                    </FormItem>
                </Col>
                <Col sm={4} md={2}>
                    <FormItem label=" " colon={false}>
                        <Button icon="save" onClick={this.addParam} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}

Cors.defaultProps = {
    form: PropType.object.required
}

export default Cors
