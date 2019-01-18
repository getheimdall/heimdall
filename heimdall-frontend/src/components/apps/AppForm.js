import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Row, Form, Input, Col, Switch, Tooltip, Button, Modal, AutoComplete, Spin, Checkbox, Tag, Table} from 'antd'

import i18n from "../../i18n/i18n"
import Loading from '../ui/Loading'
import ColorUtils from "../../utils/ColorUtils"
import ComponentAuthority from "../policy/ComponentAuthority"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"

const FormItem = Form.Item
const confirm = Modal.confirm
const Option = AutoComplete.Option
const {Column} = Table

class AppForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                payload.developer.id = Number(payload.developer.id)
                if (payload.plans) {
                    const plans = payload.plans;
                    payload.plans = plans.map((planId) => ({id: planId}))
                }

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (appId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(appId)
            }
        });
    }

    checkDeveloper = (rule, value, callback) => {
        if (this.props.developerSource.some(dev => dev.id === value)) {
            callback();
            return
        }
        callback(i18n.t('you_need_select_developer'));
    }

    render() {
        const {getFieldDecorator} = this.props.form

        const {app} = this.props
        const {loading} = this.props
        const {developerSource} = this.props
        const {fetching} = this.props
        const childrenAutoComplete = developerSource.map((dev, index) => {
            return <Option key={dev.id}>{dev.email}</Option>
        })

        let accessTokens = []
        if (app) {
            accessTokens = app.accessTokens
        }

        return (
            <Row>
                <Form>
                    {app && getFieldDecorator('id', {initialValue: app.id})(<Input type='hidden'/>)}
                    <Row gutter={24}>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: app && app.name,
                                        rules: [
                                            {required: true, message: i18n.t('please_input_app_name')},
                                            {min: 5, message: i18n.t('min_5_characters_to_name')}
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: app && app.description,
                                        type: 'number',
                                        rules: [
                                            {required: true, message: i18n.t('please_input_app_description')},
                                            {min: 5, message: i18n.t('min_5_characters_to_description')}
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])}/>)
                                }
                            </FormItem>
                        </Col>
                        {!app &&
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('client_id')}>
                                {
                                    getFieldDecorator('clientId', {
                                        initialValue: app && app.clientId,
                                        rules: [
                                            {min: 6, message: i18n.t('min_6_characters_to_client_id')}
                                        ]
                                    })(<Input
                                        disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])}/>)
                                }
                            </FormItem>
                        </Col>
                        }
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('developer')}>
                                {
                                    getFieldDecorator('developer.id', {
                                        initialValue: app && app.developer.id.toString(),
                                        validateTrigger: 'onSelect',
                                        rules: [
                                            {required: true, message: i18n.t('please_input_email_developer')},
                                            {
                                                validator: this.checkDeveloper,
                                                transform: (value) => Number(value),
                                                required: true
                                            }
                                        ]
                                    })(
                                        <AutoComplete
                                            disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])}
                                            notFoundContent={fetching ? < Spin size="small"/> : null}
                                            filterOption={false}
                                            onSearch={this.props.handleSearch}>
                                            {childrenAutoComplete}
                                        </AutoComplete>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: app ? app.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required
                                               disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            {this.props.plans && this.props.plans.content.length === 0 ? <Loading/> :
                                <FormItem label={i18n.t('plans')}>
                                    {
                                        getFieldDecorator('plans', {
                                            initialValue: app && app.plans.map(plan => plan.id)
                                        })(<Checkbox.Group className='checkbox-conductor'>
                                            {this.props.plans && this.props.plans.content.map((plan, index) => {
                                                return <Checkbox key={index} value={plan.id}
                                                                 disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])}>{plan.name}</Checkbox>
                                            })}
                                        </Checkbox.Group>)
                                    }
                                </FormItem>}
                        </Col>
                    </Row>
                </Form>
                {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_ACCESSTOKEN]) && accessTokens && accessTokens.length > 0 &&
                (
                    <div>
                        <fieldset>
                            <legend>
                                <div className="ant-card-head-title">{i18n.t('access_tokens')}</div>
                            </legend>

                            <Table dataSource={accessTokens} pagination={false} rowKey={record => record.id}>
                                <Column title={i18n.t('status')} id="status" key="status" render={(record) => (
                                    <span>
                                        <Tag color={ColorUtils.getColorActivate(record.status)}>{record.status === 'ACTIVE' ? i18n.t('active') : i18n.t('inactive')}</Tag>
                                    </span>
                                )}/>
                                <Column title={i18n.t('token')} id="token" dataIndex="code"/>
                            </Table>
                        </fieldset>
                        <br/><br/>
                    </div>
                )
                }
                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_APP]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button id="deleteApp" className="card-button" type="danger" ghost icon="delete" size="large"
                                    shape="circle"
                                    disabled={!app} onClick={app && this.showDeleteConfirm(app.id)} loading={loading}/>
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_APP]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button  id="saveApp" className="card-button" type="primary" icon="save" size="large" shape="circle"
                                    onClick={this.onSubmitForm} loading={loading}/>
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row>
        )
    }
}

AppForm.propTypes = {
    fetching: PropTypes.bool,
    loading: PropTypes.bool,
    developerSource: PropTypes.array.isRequired,
    plans: PropTypes.object.isRequired
}

AppForm.defaultProps = {
    fetching: false,
    loading: false,
    developerSource: [],
    plans: {
        content: []
    }
}

export default Form.create({})(AppForm)