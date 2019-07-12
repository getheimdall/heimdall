import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal } from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"
import ListVariablesEnvironment from './ListVariablesEnvironment'

const FormItem = Form.Item
const confirm = Modal.confirm
let uuid = 0

class EnvironmentForm extends Component {

    state = {
        variables: []
    }

    componentDidMount() {
        let variablesArray = []

        if (this.props.environment && Object.keys(this.props.environment.variables).length > 0) {
            Object.keys(this.props.environment.variables).forEach((objectKey, index) => {
                variablesArray.push({
                    key: objectKey,
                    value: this.props.environment.variables[objectKey],
                })
            });
        }

        this.setState({ ...this.state, variables: variablesArray })
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                const { getFieldValue } = this.props.form;
                const variablesForm = getFieldValue('variables');

                if (variablesForm) {
                    let variables = {}

                    variablesForm.forEach((element, index) => {
                        variables[Object.values(element)[0]] = Object.values(element)[1]
                    });

                    payload['variables'] = variables
                }

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (environmentId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(environmentId)
            }
        });
    }

    remove = (k) => {
        const { form } = this.props;
        const variablesCount = form.getFieldValue('variablesCount');

        if (variablesCount.length === 0) {
            return;
        }

        form.setFieldsValue({
            variablesCount: variablesCount.filter((value, key) => {
                return key !== k
            }
            ),
        });

        const variablesForm = form.getFieldValue('variables');
        variablesForm.splice(k, 1)

        this.setState({
            variables: variablesForm
        })
    }

    initVariables = () => {
        this.setState({ ...this.state, variables: [{ key: '', value: '' }] })
    }

    add = () => {
        const { form } = this.props;
        const variablesCount = form.getFieldValue('variablesCount');
        const variablesForm = form.getFieldValue('variables');
        const nextKeys = variablesCount.concat(uuid);
        uuid++;

        form.setFieldsValue({
            variablesCount: nextKeys,
        });

        this.setState({
            variables: variablesForm
        })
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { environment } = this.props
        const { loading } = this.props

        return (
            <Row>
                <Form>
                    {environment && getFieldDecorator('id', { initialValue: environment.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: environment && environment.name,
                                        rules: [{ required: true, message: i18n.t('please_input_your_environment_name') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('inbound_url')}>
                                {
                                    getFieldDecorator('inboundURL', {
                                        initialValue: environment && environment.inboundURL,
                                        rules: [{ required: true, message: i18n.t('please_input_your_environment_inbound') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: environment && environment.description,
                                        rules: [{ required: true, message: i18n.t('please_input_your_environment_description') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])}/>)
                                }
                            </FormItem>
                        </Col>

                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('outbound_url')}>
                                {
                                    getFieldDecorator('outboundURL', {
                                        initialValue: environment && environment.outboundURL,
                                        rules: [{ required: true, message: i18n.t('please_input_your_environment_outbound') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])}/>)
                                }
                            </FormItem>
                        </Col>

                        <Col sm={24} md={24}>
                            <fieldset>
                                <legend><div className="ant-card-head-title">{i18n.t('variables')}</div></legend>
                                {
                                    this.state.variables.length === 0 ?
                                        <Row type="flex" justify="center" align="bottom">
                                            { PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT]) &&
                                            <Col style={{ marginTop: 20 }}>
                                                {i18n.t('you_do_not_have_variables_in_this')} <b>{i18n.t('environment')}</b>! <Button id="addEnvironmentsVariable" type="dashed" className="add-tour" onClick={this.initVariables}>{i18n.t('add_variable')}</Button>
                                            </Col>
                                            }
                                            {!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT]) &&
                                            <Col style={{ marginTop: 20 }}>
                                                You don't have variables in this <b>Environment</b>
                                            </Col>
                                            }
                                        </Row>
                                        :
                                        <ListVariablesEnvironment variables={this.state.variables} form={this.props.form} add={this.add} remove={this.remove} loading={this.props.loading} />
                                }
                            </fieldset>
                        </Col>

                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('status')}>
                                {
                                    getFieldDecorator('status', {
                                        initialValue: environment ? environment.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])}/>)
                                }
                            </FormItem>
                        </Col>

                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_ENVIRONMENT]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button id="deleteEnvironment" className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!environment} onClick={environment && this.showDeleteConfirm(environment.id)} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button id="saveEnvironment" className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row>
        )
    }
}

EnvironmentForm.propTypes = {
    loading: PropTypes.bool
}

EnvironmentForm.defaultProps = {
    loading: false
}

export default Form.create({})(EnvironmentForm)
