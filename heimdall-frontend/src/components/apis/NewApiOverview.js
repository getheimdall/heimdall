import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Input, Form, Col, Row, Checkbox, Switch } from 'antd'

import i18n from "../../i18n/i18n"

const FormItem = Form.Item

class NewApiOverview extends Component {

    componentDidMount() {
        this.props.onRef(this)
    }

    componentWillUnmount() {
        this.props.onRef(undefined)
    }

    onSubmitOverview() {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                if (payload.environments) {
                    payload.environments = payload.environments.map((env) => ({ id: env }))
                }
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'

                this.props.submit(payload)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form

        return (
            <Row className="h-row no-mobile-padding">
                <Form id="api_definitions--edit">
                    {
                        getFieldDecorator('id')(<Input type='hidden' />)
                    }
                    <Col span={12} sm={24} md={12}>
                        <Row gutter={16}>
                            <Col sm={24} md={15}>
                                <FormItem label={i18n.t('api_name')}>
                                    {
                                        getFieldDecorator('name', {
                                            rules: [{ required: true, message: i18n.t('please_input_your_api_name') }]
                                        })(<Input />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={5}>
                                <FormItem label={i18n.t('api_version')}>
                                    {
                                        getFieldDecorator('version', {
                                            rules: [{ required: true, message: i18n.t('please_input_your_api_version') }]
                                        })(<Input />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={15}>
                                <FormItem label={i18n.t('description')}>
                                    {
                                        getFieldDecorator('description', {
                                            rules: [{ required: true, message: i18n.t('please_input_your_api_description') }]
                                        })(<Input />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={5}>
                                <FormItem label={i18n.t('base_path')}>
                                    {
                                        getFieldDecorator('basePath', {
                                            rules: [{ required: true, message: 'Please input your api base path!' }]
                                        })(<Input addonBefore={"/"} placeholder={i18n.t('base_path')} />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={5}>
                                <FormItem label={i18n.t('status')}>
                                    {
                                        getFieldDecorator('status', {
                                            initialValue: this.props.api.status === 'ACTIVE',
                                            valuePropName: 'checked',
                                            rules: [{ required: true, message: i18n.t('please_select_your_api_status') }]
                                        })(<Switch required />)
                                    }
                                </FormItem>
                            </Col>
                        </Row>
                    </Col>
                    <Col span={12} sm={24} md={12}>
                        <Row gutter={16}>
                            <Col sm={24}>
                                <FormItem label={i18n.t('environments')}>
                                    {
                                        getFieldDecorator('environments', {
                                            rules: [{ required: true, message: i18n.t('please_select_an_environment') }]
                                        })(<Checkbox.Group className='checkbox-conductor' options={this.props.environments} />)
                                    }
                                </FormItem>
                            </Col>
                        </Row>
                    </Col>
                </Form>
            </Row>
        )
    }
}

NewApiOverview.propTypes = {
    next: PropTypes.func.isRequired
}

const mapPropsToFields = (props) => {
    return {
        id: Form.createFormField({ ...props.api.id }),
        name: Form.createFormField({ ...props.api.name }),
        version: Form.createFormField({ ...props.api.version }),
        status: Form.createFormField({ ...props.api.status }),
        description: Form.createFormField({ ...props.api.description }),
        basePath: Form.createFormField({ ...props.api.basePath }),
        environments: Form.createFormField({ ...props.api.environments })
    }
}

const WrappedOverviewForm = Form.create({ mapPropsToFields })(NewApiOverview)

export default WrappedOverviewForm