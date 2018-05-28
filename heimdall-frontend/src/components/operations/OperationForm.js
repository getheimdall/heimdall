import React, { Component } from 'react'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { Row, Form, Col, Input, Select } from 'antd'
import PropTypes from 'prop-types'
import { resetOperation, getOperation } from '../../actions/operations';
import Loading from '../ui/Loading';

const FormItem = Form.Item
const Option = Select.Option

class OperationForm extends Component {

    componentDidMount() {
        if (this.props.operationId !== 0) {
            this.props.getOperation(this.props.idApi, this.props.idResource, this.props.operationId)
        } else {
            this.props.resetOperation()
        }
        this.props.onRef(this)
    }

    componentWillUnmount() {
        this.props.onRef(undefined)
        this.props.resetOperation()
    }

    onSubmitForm() {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.onSubmit(payload)
            }
        });
    }

    render() {
        if (this.props.operationId !== 0 && !this.props.operation) {
            return <Loading />
        }

        const { getFieldDecorator } = this.props.form
        return (
            <Row >
                <Form>
                    {this.props.operation && getFieldDecorator('id', { initialValue: this.props.operation.id })(<Input type='hidden' />)}
                    <Row>
                        <Col sm={24}>
                            <FormItem label="Method">
                                {getFieldDecorator('method', {
                                    rules: [{ required: true, message: 'Please, input operation path!' }],
                                    initialValue: this.props.operation && this.props.operation.method.toUpperCase()
                                })(
                                    <Select>
                                        <Option value="GET">GET</Option>
                                        <Option value="POST">POST</Option>
                                        <Option value="PUT">PUT</Option>
                                        <Option value="PATCH">PATCH</Option>
                                        <Option value="DELETE">DELETE</Option>
                                    </Select>
                                )}
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label="Path">
                                {
                                    getFieldDecorator('path', {
                                        initialValue: this.props.operation && this.props.operation.path,
                                        rules: [{ required: true, message: 'Please input your api path!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label="Description">
                                {
                                    getFieldDecorator('description', {
                                        initialValue: this.props.operation && this.props.operation.description
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </Row>
        )
    }
}

OperationForm.propTypes = {
    idApi: PropTypes.number.isRequired,
    idResource: PropTypes.number.isRequired
}

const mapStateToProps = state => {
    return {
        operation: state.operations.operation
    }
}

const mapDispatchToProps = dispatch => {
    return {
        resetOperation: bindActionCreators(resetOperation, dispatch),
        getOperation: bindActionCreators(getOperation, dispatch)
    }
}

const WrappedOperationForm = Form.create({})(OperationForm)

export default connect(mapStateToProps, mapDispatchToProps)(WrappedOperationForm)