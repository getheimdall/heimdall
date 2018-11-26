import React, { Component } from 'react'
import { Row, Form, Col, Input } from 'antd'
import { bindActionCreators } from 'redux'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import i18n from "../../i18n/i18n"
import Loading from '../ui/Loading'
import { isEmpty } from '../../utils/CommonUtils'
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"
import { toggleModal, resetResource, getResource, save, update } from '../../actions/resources'

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
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: this.props.resource ? this.props.resource.name : '',
                                        rules: [{ required: true, message: i18n.t('please_input_api_name') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_RESOURCE, privileges.PRIVILEGE_UPDATE_RESOURCE])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: this.props.resource ? this.props.resource.description : ''
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_RESOURCE, privileges.PRIVILEGE_UPDATE_RESOURCE])}/>)
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