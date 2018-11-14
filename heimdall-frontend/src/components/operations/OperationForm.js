import React, { Component } from 'react'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { Row, Form, Col, Input, Select } from 'antd'
import PropTypes from 'prop-types'

import i18n from "../../i18n/i18n"
import Loading from '../ui/Loading'
import { resetOperation, getOperation } from '../../actions/operations'

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
                            <FormItem label={i18n.t('method')}>
                                {getFieldDecorator('method', {
                                    rules: [{ required: true, message: i18n.t('please_input_operation_method') }],
                                    initialValue: this.props.operation && this.props.operation.method.toUpperCase()
                                })(
                                    <Select>
                                        <Option value="GET">GET</Option>
                                        <Option value="POST">POST</Option>
                                        <Option value="PUT">PUT</Option>
                                        <Option value="PATCH">PATCH</Option>
                                        <Option value="DELETE">DELETE</Option>
                                        <Option value="ALL">ALL</Option>
                                    </Select>
                                )}
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label={i18n.t('path')}>
                                {
                                    getFieldDecorator('path', {
                                        initialValue: this.props.operation && this.props.operation.path,
                                        rules: [{ required: true, message: i18n.t('please_input_operation_path') }]
                                    })(<Input addonBefore={this.props.apiBasepath + "/"} required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label={i18n.t('description')}>
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
    idResource: PropTypes.number.isRequired,
    apiBasepath: PropTypes.number.isRequired
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