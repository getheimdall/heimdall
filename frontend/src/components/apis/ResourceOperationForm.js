import React from 'react'
import { Form, Row, Col, Select, Input, Checkbox, Button, Tooltip, Icon } from 'antd'
import ReactCSSTransitionGroup from 'react-addons-css-transition-group'


const FormItem = Form.Item
const Option = Select.Option

const ResourceOperationForm = ({
    handleOperationMethod,
    handleOperationPath,
    handleOperationDescription,
    handleOperationAsync,
    toggleForm,
    validateForm,
    form
}) => {
    return (
        <ReactCSSTransitionGroup
            transitionName="appear"
            transitionAppear={true}
            transitionAppearTimeout={500}
            transitionEnter={false}
            transitionLeave={false}
        >
            <Form id="new_operation">
                <Row gutter={20} type="flex" justify="space-between" align="bottom">
                    <Col sm={12} md={4}>
                        <FormItem
                            label="Method"
                        >
                            {form.getFieldDecorator('operationMethod', {
                                rules: [{ required: true, message: 'Please, input operation path!' }],
                                initialValue: 'get'
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

                    <Col sm={12} md={8}>
                        <FormItem label="Path">
                            {form.getFieldDecorator('operationPath', {
                                rules: [{ required: true, message: 'Please, input operation path!' }]
                            })(
                                <Input
                                    id="operation_path"
                                    name="operation_path"
                                    onChange={handleOperationPath}
                                />
                            )}
                        </FormItem>
                    </Col>

                    <Col sm={12} md={8}>
                        <FormItem label="Description">
                            <Input
                                id="operation_description"
                                name="operation_description"
                                onChange={handleOperationDescription}
                                defaultValue=""
                            />
                        </FormItem>
                    </Col>

                    <Col sm={24} md={4}>
                        <FormItem>
                            <Checkbox
                                id="operation_async"
                                name="operation_async"
                                onChange={handleOperationAsync}
                                defaultChecked={false}
                            >
                                Async
                            </Checkbox>
                        </FormItem>
                    </Col>

                    <Col sm={24}>
                        <Button.Group size="small">
                            <Button id="saveResourceOperation" type="primary" onClick={validateForm}>
                                <Icon type="save" /> Save operation
                            </Button>
                            <Tooltip title="Cancel">
                                <Button id="cancelResourceOperation" type="primary" icon="close" ghost onClick={toggleForm} />
                            </Tooltip>
                        </Button.Group>
                    </Col>
                </Row>
            </Form>
        </ReactCSSTransitionGroup>
    )
}

const WrapForm = Form.create()(ResourceOperationForm)

export default WrapForm