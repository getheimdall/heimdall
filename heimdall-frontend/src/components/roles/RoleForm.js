import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Button, Col, Form, Input, Modal, Row, Tooltip, Transfer} from 'antd'
import ComponentAuthority from "../policy/ComponentAuthority";
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";
import {privileges} from "../../constants/privileges-types";
import Loading from "../ui/Loading";
import i18n from "../../i18n/i18n";

const FormItem = Form.Item
const confirm = Modal.confirm

class RoleForm extends Component {

    state = {
        storagePrivileges: [],
        privilegesRole: []
    }

    componentDidMount() {
        this.mountTransfer()
    }

    mountTransfer = () => {
        let storagePrivileges = []
        let privilegesRole = []
        if (this.props.privileges) {
            storagePrivileges = this.props.privileges.map(p => {
                return {key: p.id, title: p.name, disabled: !PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ROLE, privileges.PRIVILEGE_UPDATE_ROLE])}
            })
        }
        if (this.props.role && this.props.role.privileges) {
            privilegesRole = this.props.role.privileges.map(p => p.id)
        }

        this.setState({ ...this.state, storagePrivileges: storagePrivileges, privilegesRole: privilegesRole})
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.privileges = this.state.privilegesRole.map(p => {
                    return {id: p}
                })
                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (roleId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(roleId)
            }
        });
    }

    filterOption = (inputValue, option) => {
        return option.title.toUpperCase().includes(inputValue.toUpperCase())
    }

    handleChangeTransfer = (targetKeys) => {
        this.setState({...this.state, privilegesRole: targetKeys})
    }

    render() {
        const {getFieldDecorator} = this.props.form
        const {role, loading} = this.props

        return (
            <Row>
                <Form>
                    {role && getFieldDecorator('id', {initialValue: role.id})(<Input type='hidden'/>)}
                    <Row gutter={24} type="flex" justify="space-around" align="top">
                        <Col sm={24} md={8}>
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: role && role.name,
                                        rules: [
                                            {required: true, message: i18n.t('please_input_role_name')},
                                            {min: 4, message: i18n.t('min_4_characters_to_name')}
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ROLE, privileges.PRIVILEGE_UPDATE_ROLE])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={16}>
                            {
                                !this.props.privileges && this.props.privileges.length === 0 ? <Loading/> :
                                    <FormItem>
                                        <Transfer
                                            showSearch
                                            titles={[i18n.t('available_privileges'), i18n.t('attributed_privileges')]}
                                            onChange={this.handleChangeTransfer}
                                            filterOption={this.filterOption}
                                            dataSource={this.state.storagePrivileges}
                                            listStyle={{width: '45%', height: '300px'}}
                                            targetKeys={this.state.privilegesRole}
                                            render={i => i.title}
                                        />
                                    </FormItem>
                            }
                        </Col>
                    </Row>
                </Form>
                <br/>
                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_ROLE]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button className="card-button" type="danger" ghost icon="delete" size="large"
                                    shape="circle"
                                    disabled={!role} onClick={role && this.showDeleteConfirm(role.id)}
                                    loading={loading}/>
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority
                        privilegesAllowed={[privileges.PRIVILEGE_CREATE_ROLE, privileges.PRIVILEGE_UPDATE_ROLE]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button className="card-button" type="primary" icon="save" size="large" shape="circle"
                                    onClick={this.onSubmitForm} loading={loading}/>
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row>
        )
    }
}

RoleForm.propTypes = {
    loading: PropTypes.bool,
    role: PropTypes.object.isRequired,
    privileges: PropTypes.array
}

RoleForm.defaultProps = {
    loading: false,
    role: {},
    privileges: []
}

export default Form.create({})(RoleForm)