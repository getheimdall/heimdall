import React, { Component } from 'react'
import { Form, Row, Col, Input, Select } from 'antd'

const FormItem = Form.Item
const Option = Select.Option

class EditOperation extends Component {

    componentWillReceiveProps() {
        this.props.form.resetFields()
    }
    render() {
        const {
            operation,
            handleOperationMethod,
            handleOperationPath,
            handleOperationDescription,
        } = this.props
        const { getFieldDecorator } = this.props.form

        return (
            <Form id="edit_operation">
                <Row gutter={20} type="flex" justify="space-between" align="bottom">
                    <Col sm={24}>
                        <FormItem
                            label="Method"
                        >
                            {getFieldDecorator('operationEditMethod', {
                                rules: [{ required: true, message: 'Please, input operation path!' }],
                                initialValue: operation.method.toLowerCase()
                            })(
                                <Select
                                    style={{width:'100%'}}
                                    onChange={handleOperationMethod}
                                >
                                    <Option value="get">GET</Option>
                                    <Option value="post">POST</Option>
                                    <Option value="put">PUT</Option>
                                    <Option value="delete">DELETE</Option>
                                </Select>
                            )}
                        </FormItem>
                    </Col>

                    <Col sm={24}>
                        <FormItem label="Path">
                            {getFieldDecorator('operationEditPath', {
                                rules: [{ required: true, message: 'Please, input operation path!' }],
                                initialValue: operation.path
                            })(
                                <Input
                                    id="operation_edit_path"
                                    name="operation_edit_path"
                                    onChange={handleOperationPath}
                                />
                            )}
                        </FormItem>
                    </Col>

                    <Col sm={24}>
                        <FormItem label="Description">
                            <Input
                                id="operation_edit_description"
                                name="operation_edit_description"
                                onChange={handleOperationDescription}
                                value={operation.description}
                            />
                        </FormItem>
                    </Col>
                </Row>
            </Form>
        )
    }
}

const WrapForm = Form.create()(EditOperation)

export default WrapForm