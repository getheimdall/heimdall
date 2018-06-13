import React, { Component } from 'react'
import PropTypes from 'prop-types'

import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal } from 'antd'
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
        } else {
            variablesArray.push({
                key: '',
                value: ''
            })
        }

        this.setState({...this.state, variables: variablesArray})
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
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(environmentId)
            }
        });
    }

    remove = (k) => {
        const { form } = this.props;
        const variablesCount = form.getFieldValue('variablesCount');

        if (variablesCount.length === 1) {
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
                            <FormItem label="Name">
                                {
                                    getFieldDecorator('name', {
                                        initialValue: environment && environment.name,
                                        rules: [{ required: true, message: 'Please input your environment name!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Inbound URL">
                                {
                                    getFieldDecorator('inboundURL', {
                                        initialValue: environment && environment.inboundURL,
                                        rules: [{ required: true, message: 'Please input your environment inbound!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Description">
                                {
                                    getFieldDecorator('description', {
                                        initialValue: environment && environment.description,
                                        rules: [{ required: true, message: 'Please input your environment description!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>

                        <Col sm={24} md={12}>
                            <FormItem label="Outbound URL">
                                {
                                    getFieldDecorator('outboundURL', {
                                        initialValue: environment && environment.outboundURL,
                                        rules: [{ required: true, message: 'Please input your environment outbound!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>

                        <Col sm={24} md={24}>
                            <fieldset>
                                <legend><div className="ant-card-head-title">Variables</div></legend>
                                <ListVariablesEnvironment variables={this.state.variables} form={this.props.form} add={this.add} remove={this.remove} loading={this.props.loading} />
                            </fieldset>
                        </Col>

                        <Col sm={24} md={24}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: environment ? environment.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required />)
                                }
                            </FormItem>
                        </Col>

                    </Row>
                </Form>
                
                <Row type="flex" justify="end">
                    <Tooltip title="Delete">
                        <Button className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!environment} onClick={environment && this.showDeleteConfirm(environment.id)} loading={loading} />
                    </Tooltip>
                    <Tooltip title="Save">
                        <Button className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                    </Tooltip>
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
