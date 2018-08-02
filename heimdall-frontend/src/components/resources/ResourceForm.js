import React, { Component } from 'react'
import { Row, Form, Col, Input } from 'antd'
import { bindActionCreators } from 'redux'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { isEmpty } from '../../utils/CommonUtils'

import { toggleModal, resetResource, getResource, save, update } from '../../actions/resources'
import Loading from '../ui/Loading'

const FormItem = Form.Item

class ResourceForm extends Component {

    componentDidMount() {
        if (this.props.resourceId !== 0) {
            this.props.getResource(this.props.idApi, this.props.resourceId)
        } else {
            this.props.resetResource()
        }
        this.props.onRef(this)
    }

    componentWillUnmount() {
        this.props.onRef(undefined)
        this.props.resetResource()
    }

    onSubmitResource() {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {

                // this.props.onSubmit(payload)
                if (this.props.resourceId === 0) {
                    this.props.save(this.props.idApi, payload)
                } else {
                    this.props.update(this.props.idApi, payload)
                }
                // this.props.toggleModal(false)
            }
        });
    }

    render() {
        if (this.props.resourceId !== 0) {
            if (isEmpty(this.props.resource)) return <Loading />
        }

        const { getFieldDecorator } = this.props.form
        return (
            <Row >
                <Form>
                    {this.props.resource ? getFieldDecorator('id', { initialValue: this.props.resource.id })(<Input type='hidden' />) : null}
                    <Row>
                        <Col sm={24}>
                            <FormItem label="Name">
                                {
                                    getFieldDecorator('name', {
                                        initialValue: this.props.resource ? this.props.resource.name : '',
                                        rules: [{ required: true, message: 'Please input your api name!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label="Description">
                                {
                                    getFieldDecorator('description', {
                                        initialValue: this.props.resource ? this.props.resource.description : ''
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

ResourceForm.propTypes = {
    idApi: PropTypes.number.isRequired
}

const mapStateToProps = state => {
    return {
        resource: state.resources.resource
    }
}

const mapDispatchToProps = dispatch => {
    return {
        toggleModal: bindActionCreators(toggleModal, dispatch),
        resetResource: bindActionCreators(resetResource, dispatch),
        getResource: bindActionCreators(getResource, dispatch),
        save: bindActionCreators(save, dispatch),
        update: bindActionCreators(update, dispatch)
    }
}

const WrappedResourceForm = Form.create({})(ResourceForm)

export default connect(mapStateToProps, mapDispatchToProps)(WrappedResourceForm)