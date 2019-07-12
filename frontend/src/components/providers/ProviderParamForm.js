import React from 'react'
import { connect } from 'react-redux'
import {Col, Form, Input, Row, Select} from 'antd'

import i18n from "../../i18n/i18n"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"
import UUID from "../../utils/UUID"
import { sendNotification } from "../../actions/providers"

const FormItem = Form.Item

class ProviderParamForm extends React.Component {

    state = { providerParam: null }

    componentDidMount() {
        this.props.onRef(this)
        const { providerParamId, providerParams } = this.props

        if (providerParamId) {

            const providerParam = providerParams.find(p => p.id === providerParamId || p.uuid === providerParamId)
            this.setState({ ...this.state, providerParam: providerParam })
        }
    }

    componentWillUnmount() {
        this.props.onRef(undefined)
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {

                const providerParams = this.props.providerParams

                if (payload.id || payload.uuid) {
                    if (providerParams.find(p => p.name === payload.name && p.location === payload.location && (payload.id !== p.id || payload.uuid !== p.uuid))) {
                        this.props.dispatch(sendNotification({ type: 'error', message: i18n.t('provider_param_already_exist') }))
                        this.props.onSubmit()
                    } else {
                        this.props.onSubmit(payload)
                    }
                } else {
                    if (providerParams.find(p => p.name === payload.name && p.location === payload.location)) {
                        this.props.dispatch(sendNotification({ type: 'error', message: i18n.t('provider_param_already_exist') }))
                        this.props.onSubmit()
                    } else {
                        if (!payload.id) {
                            payload.uuid = UUID.generate()
                        }
                        this.props.onSubmit(payload)
                    }
                }
            }
        })
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { providerParam } = this.state

        return (
            <Row>
                <Form>
                    {providerParam && providerParam.id && getFieldDecorator('id', {initialValue: providerParam.id})(<Input type='hidden'/>)}
                    {providerParam && providerParam.uuid && getFieldDecorator('uuid', {initialValue: providerParam.uuid})(<Input type='hidden'/>)}
                    <Row gutter={24}>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: providerParam && providerParam.name,
                                        rules: [
                                            {required: true, message: i18n.t('please_input_provider_param_name')},
                                            {min: 4, message: i18n.t('min_4_characters_to_name')}
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('location')}>
                                {
                                    getFieldDecorator('location', {
                                        initialValue: providerParam && providerParam.location,
                                    })(<Select disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER])}>
                                        <Select.Option value="BODY">BODY</Select.Option>
                                        <Select.Option value="HEADER">HEADER</Select.Option>
                                    </Select>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('value')}>
                                {
                                    getFieldDecorator('value', {
                                        initialValue: providerParam && providerParam.value
                                    })(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER])}/>)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </Row>
        )
    }
}

const mapStateToProps = state => {
    return {
        loading: state.providers.loading
    }
}

export default connect(mapStateToProps)(Form.create({})(ProviderParamForm))