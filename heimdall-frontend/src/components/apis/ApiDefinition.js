import React, { Component } from 'react'
import { Form, Input, Row, Col, Checkbox, Switch, Tooltip, Button, Modal } from 'antd'

// import FloatMenu from '../ui/FloatMenu'
import i18n from "../../i18n/i18n"

const FormItem = Form.Item
const confirm = Modal.confirm;

class ApiDefinition extends Component {

    constructor(props) {
        super(props)

        this.onSubmitApi = this.onSubmitApi.bind(this)
        this.deleteApi = this.deleteApi.bind(this)
        this.showDeleteConfirm = this.showDeleteConfirm.bind(this)
    }

    onSubmitApi() {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                if (payload.environments) {
                    let environments = payload.environments;
                    payload.environments = environments.map((env) => ({ id: env }))
                }
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'

                this.props.submit(payload)
            }
        });
    }

    deleteApi() {
        this.props.deleteApi(this.props.api.id)
    }

    showDeleteConfirm() {
        const deleteApi = this.props.deleteApi
        const idApi = this.props.api.id

        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            content: (
                <div>
                    <p>{ i18n.t('deleting_an_api_delete_resources_operations_interceptors_middlewares') }</p>
                    <p>{ i18n.t('this_operation_can_not_be_reverted') }</p>
                </div>
            ),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk() {
                deleteApi(idApi)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { api } = this.props

        const options = this.props.environments.map((env, index) => {
            const labered = env.name + ' [' + env.inboundURL + ']'
            return { label: labered, value: env.id }
        })
        return (
            <Row className="h-row no-mobile-padding">
                <Row>
                    <Form id="api_definitions--edit">
                        {
                            getFieldDecorator('id', {
                                initialValue: api.id
                            })(<Input type='hidden' />)
                        }
                        <Col sm={24} md={12}>
                            <Row gutter={16}>
                                <Col sm={24} md={15}>
                                    <FormItem label={i18n.t('api_name')}>
                                        {
                                            getFieldDecorator('name', {
                                                initialValue: api.name,
                                                rules: [{ required: true, message: i18n.t('please_input_your_api_name') }]
                                            })(<Input />)
                                        }
                                    </FormItem>
                                </Col>

                                <Col sm={24} md={5}>
                                    <FormItem label={i18n.t('api_version')}>
                                        {
                                            getFieldDecorator('version', {
                                                initialValue: api.version,
                                                rules: [{ required: true, message: i18n.t('please_input_your_api_version') }]
                                            })(<Input />)
                                        }
                                    </FormItem>
                                </Col>

                                <Col sm={24} md={15}>
                                    <FormItem label={i18n.t('description')}>
                                        {
                                            getFieldDecorator('description', {
                                                initialValue: api.description,
                                                rules: [{ required: true, message: i18n.t('please_input_your_api_description') }]
                                            })(<Input />)
                                        }
                                    </FormItem>
                                </Col>

                                <Col sm={24} md={5}>
                                    <FormItem label={i18n.t('base_path')}>
                                        {
                                            getFieldDecorator('basePath', {
                                                initialValue: api.basePath.replace("/", ""),
                                                rules: [{ required: true, message: i18n.t('please_input_your_api_base_path') }]
                                            })(<Input addonBefore={"/"}/>)
                                        }
                                    </FormItem>
                                </Col>

                                <Col sm={24} md={5}>
                                    <FormItem label={i18n.t('status')}>
                                        {
                                            getFieldDecorator('status', {
                                                initialValue: api.status === 'ACTIVE',
                                                valuePropName: 'checked',
                                                rules: [{ required: true, message: i18n.t('please_select_your_api_status') }]
                                            })(<Switch required />)
                                        }
                                    </FormItem>
                                </Col>
                            </Row>
                        </Col>
                        <Col sm={24} md={12}>
                            <Row gutter={16}>
                                <Col sm={24}>
                                    <FormItem label={i18n.t('environments')}>
                                        {
                                            getFieldDecorator('environments', {
                                                initialValue: api.environments ? api.environments.map(env => env.id) : [],
                                                rules: [{ required: true, message: i18n.t('please_select_an_environment') }]
                                            })(<Checkbox.Group className='checkbox-conductor' options={options} />)
                                        }
                                    </FormItem>
                                </Col>
                            </Row>
                        </Col>
                    </Form>
                </Row>

                <Row type="flex" justify="end">
                    <Tooltip title={i18n.t('delete')}>
                        <Button id="deleteApi" className="card-button" type="danger" ghost icon="delete" onClick={this.showDeleteConfirm} size="large" shape="circle" />
                    </Tooltip>
                    <Tooltip title={i18n.t('save')}>
                        <Button id="saveApi" className="card-button" type="primary" icon="save" onClick={this.onSubmitApi} size="large" shape="circle" />
                    </Tooltip>
                </Row>
            </Row>
        )
    }
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

const WrappedApiDefinitionForm = Form.create({ mapPropsToFields })(ApiDefinition)

export default WrappedApiDefinitionForm