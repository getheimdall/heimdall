import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Input, Form, Col, Row, Checkbox, Switch } from 'antd'

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
                                <FormItem label="API Name">
                                    {
                                        getFieldDecorator('name', {
                                            rules: [{ required: true, message: 'Please input your api name!' }]
                                        })(<Input />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={5}>
                                <FormItem label="API version">
                                    {
                                        getFieldDecorator('version', {
                                            rules: [{ required: true, message: 'Please input your api version!' }]
                                        })(<Input />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={15}>
                                <FormItem label="Description">
                                    {
                                        getFieldDecorator('description', {
                                            rules: [{ required: true, message: 'Please input your api description!' }]
                                        })(<Input />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={5}>
                                <FormItem label="Base path">
                                    {
                                        getFieldDecorator('basePath', {
                                            rules: [{ required: true, message: 'Please input your api base path!' }]
                                        })(<Input placeholder="/basepath" />)
                                    }
                                </FormItem>
                            </Col>

                            <Col sm={24} md={5}>
                                <FormItem label="Status">
                                    {
                                        getFieldDecorator('status', {
                                            initialValue: this.props.api.status === 'ACTIVE',
                                            valuePropName: 'checked',
                                            rules: [{ required: true, message: 'Please input your api base path!' }]
                                        })(<Switch required />)
                                    }
                                </FormItem>
                            </Col>
                        </Row>
                    </Col>
                    <Col span={12} sm={24} md={12}>
                        <Row gutter={16}>
                            <Col sm={24}>
                                <FormItem label="Environments">
                                    {
                                        getFieldDecorator('environments', {
                                            rules: [{ required: true, message: 'Please select an environment' }]
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