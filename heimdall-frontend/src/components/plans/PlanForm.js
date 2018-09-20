import React, { Component } from 'react'
import PropTypes from 'prop-types'

import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal, AutoComplete, Spin, Icon } from 'antd'

const FormItem = Form.Item
const confirm = Modal.confirm
const Option = AutoComplete.Option

class PlanForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                payload.api.id = Number(payload.api.id)
                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (planId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(planId)
            }
        });
    }

    checkApi = (rule, value, callback) => {
        if (this.props.apiSource.some(api => api.id === value)) {
            callback();
            return
        }
        callback('You need select an api!');
    }

    render() {
        const { getFieldDecorator } = this.props.form

        const { plan } = this.props
        const { loading } = this.props
        const { apiSource } = this.props
        const { fetching } = this.props
        const apiAutocompleteSource = apiSource.map((api, index) => {
            return <Option key={api.id}>{api.name}</Option>
        })

        return (
            <Row>
                <Form>
                    {plan && getFieldDecorator('id', { initialValue: plan.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={24} >
                            <FormItem label="Name">
                                {
                                    getFieldDecorator('name', {
                                        initialValue: plan && plan.name,
                                        rules: [
                                            { required: true, message: 'Please input an plan name!' },
                                            { min: 5, message: 'Min of 5 Characters to name!' }
                                        ]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24} >
                            <FormItem label="Description">
                                {
                                    getFieldDecorator('description', {
                                        initialValue: plan && plan.description,
                                        rules: [
                                            { required: true, message: 'Please input an plan description!' },
                                            { min: 5, message: 'Min of 5 Characters to description!' }
                                        ]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label="Api">
                                {
                                    getFieldDecorator('api.id', {
                                        initialValue: plan && plan.api.id.toString(),
                                        validateTrigger: 'onSelect',
                                        rules: [
                                            { validator: this.checkApi, transform: (value) => Number(value), required: true }
                                        ]
                                    })(
                                        <AutoComplete
                                            notFoundContent={fetching ? < Spin size="small" /> : null}
                                            filterOption={false}
                                            dataSource={apiAutocompleteSource}
                                            onSearch={this.props.handleSearch}
                                            optionLabelProp="children">
                                            {/* {apiAutocompleteSource} */}
                                            <Input addonBefore={<Icon type="search" />} spellCheck={false} />
                                        </AutoComplete>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={5}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: plan ? plan.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <Tooltip title="Delete">
                        <Button id="deletePlan" className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!plan} onClick={plan && this.showDeleteConfirm(plan.id)} loading={loading} />
                    </Tooltip>
                    <Tooltip title="Save">
                        <Button id="savePlan" className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                    </Tooltip>
                </Row>
            </Row >
        )
    }
}

PlanForm.propTypes = {
    fetching: PropTypes.bool,
    loading: PropTypes.bool,
    apiSource: PropTypes.array.isRequired
}

PlanForm.defaultProps = {
    fetching: false,
    loading: false,
    apiSource: []
}

export default Form.create({})(PlanForm)