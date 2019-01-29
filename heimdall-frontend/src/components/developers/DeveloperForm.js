import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal } from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import { privileges } from '../../constants/privileges-types'

const FormItem = Form.Item
const confirm = Modal.confirm;

class DeveloperForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (developerId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(developerId)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form

        const { developer } = this.props
        const { loading } = this.props

        return (
            <Row>
                <Form>
                    {developer && getFieldDecorator('id', { initialValue: developer.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={24} >
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: developer && developer.name,
                                        rules: [{ required: true, message: i18n.t('please_input_your_name') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_DEVELOPER, privileges.PRIVILEGE_UPDATE_DEVELOPER])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('email')}>
                                {
                                    getFieldDecorator('email', {
                                        initialValue: developer && developer.email,
                                        rules: [{ required: true, type: 'email', message: i18n.t('please_input_valid_email') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_DEVELOPER, privileges.PRIVILEGE_UPDATE_DEVELOPER])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={5}>
                            <FormItem label={i18n.t('status')}>
                                {
                                    getFieldDecorator('status', {
                                        initialValue: developer ? developer.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_DEVELOPER, privileges.PRIVILEGE_UPDATE_DEVELOPER])}/>)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_DEVELOPER]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button id="deleteDeveloper" className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!developer} onClick={developer && this.showDeleteConfirm(developer.id)} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_DEVELOPER, privileges.PRIVILEGE_UPDATE_DEVELOPER]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button id="saveDeveloper" className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row>
        )
    }
}

DeveloperForm.propTypes = {
    loading: PropTypes.bool
}

DeveloperForm.defaultProps = {
    loading: false
}

export default Form.create({})(DeveloperForm)