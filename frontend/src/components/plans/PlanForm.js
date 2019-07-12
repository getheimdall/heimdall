import React, { Component } from 'react'
import PropTypes from 'prop-types'
import {Row, Form, Input, Col, Switch, Tooltip, Button, Modal, AutoComplete, Spin, Icon, Transfer} from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"
import {scopeService} from "../../services"

const FormItem = Form.Item
const confirm = Modal.confirm
const Option = AutoComplete.Option

class PlanForm extends Component {

    state = {
        transferLoading: false,
        transferDataSource: [],
        transferSelected: [],
        apiId: 0
    }

    componentDidMount() {
        if (this.props.plan && this.props.plan.api && this.props.plan.api.id) {
            this.setState({ ...this.state, apiId: this.props.plan.api.id})
        }
    }

    componentWillUpdate(nextProps, nextState) {

        if (nextState.apiId !== this.state.apiId) {
            this.mountTransfer(nextState.apiId)
        }
    }

    mountTransfer = apiId => {

        let transferDataSource = []
        let transferSelected = []

        if (apiId !== 0) {
            scopeService.getScopes({}, apiId)
                .then(data => {
                    transferDataSource = data.map(scope => {
                        return {
                            key: scope.id,
                            title: scope.name,
                            description: scope.description,
                            disabled: !PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_SCOPE])
                        }
                    })

                    if (this.props.plan && this.props.plan.scopes) {
                        transferSelected = this.props.plan.scopes.map(p => p.id)
                    }
                    
                    this.setState({ ...this.state, transferDataSource: transferDataSource, transferSelected: transferSelected, transferLoading: false})
                })
        } else {
            this.setState({ ...this.state, transferDataSource: transferDataSource, transferSelected: transferSelected, transferLoading: false})
        }

    }

    filterOption = (inputValue, option) => {
        return option.title.toUpperCase().includes(inputValue.toUpperCase())
    }

    handleChangeTransfer = (targetKeys) => {
        this.setState({ ...this.state, transferSelected: targetKeys })
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                payload.api.id = Number(payload.api.id)
                payload.scopes = this.state.transferSelected.map(p => {
                    return { id: p }
                })

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (planId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(planId)
            }
        });
    }

    checkApi = (rule, value, callback) => {
        if (this.props.apiSource.some(api => api.id === value)) {
            callback();
            return
        }
        callback(i18n.t('you_need_select_api'))
    }

    handleSelectApi = apiId => {
        this.setState({ ...this.state, apiId: apiId })
    }

    render() {
        const { getFieldDecorator } = this.props.form

        const { plan } = this.props
        const { loading } = this.props
        const { apiSource } = this.props
        const { fetching } = this.props

        const apiAutocompleteSource = apiSource.map((api, index) => {
            return <Option key={api.id}>{api.name}</Option>
        })

        return (
            <Row>
                <Form>
                    {plan && getFieldDecorator('id', { initialValue: plan.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={24} >
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: plan && plan.name,
                                        rules: [
                                            { required: true, message: i18n.t('please_input_plan_name') },
                                            { min: 5, message: i18n.t('min_5_characters_to_name') }
                                        ]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24} >
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: plan && plan.description,
                                        rules: [
                                            { required: true, message: i18n.t('please_input_plan_description') },
                                            { min: 5, message: i18n.t('min_5_characters_to_description') }
                                        ]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('api')}>
                                {
                                    getFieldDecorator('api.id', {
                                        initialValue: plan && plan.api.id.toString(),
                                        validateTrigger: 'onSelect',
                                        rules: [
                                            { validator: this.checkApi, transform: (value) => Number(value), required: true }
                                        ]
                                    })(
                                        <AutoComplete
                                            disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])}
                                            notFoundContent={fetching ? < Spin size="small" /> : null}
                                            filterOption={false}
                                            dataSource={apiAutocompleteSource}
                                            onSearch={this.props.handleSearch}
                                            onSelect={this.handleSelectApi}
                                            optionLabelProp="children">
                                            {/* {apiAutocompleteSource} */}
                                            <Input addonBefore={<Icon type="search" />} spellCheck={false} />
                                        </AutoComplete>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            {
                                this.state.apiId === 0 && <p> {i18n.t('you_need_select_api')} </p>
                            }
                            {
                                this.state.apiId !== 0 &&
                                    <FormItem label={i18n.t('scopes')}>
                                        <Transfer
                                            showSearch
                                            titles={[i18n.t('available_scopes'), i18n.t('attributed_scopes')]}
                                            onChange={this.handleChangeTransfer}
                                            filterOption={this.filterOption}
                                            dataSource={this.state.transferDataSource}
                                            listStyle={{ width: '48%', height: '300px' }}
                                            targetKeys={this.state.transferSelected}
                                            render={i => (
                                                <span className="custom-item">
                                                    {i.title} {i.description && `- ${i.description}`}
                                                </span>)
                                            }
                                        />
                                    </FormItem>
                            }
                        </Col>
                        <Col sm={12} md={4}>
                            <FormItem label={i18n.t('status')}>
                                {
                                    getFieldDecorator('status', {
                                        initialValue: plan ? plan.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={12} md={4}>
                            <FormItem label={i18n.t('default_plan_this_api')}>
                                {
                                    getFieldDecorator('defaultPlan', {
                                        initialValue: plan && plan.defaultPlan,
                                        valuePropName: 'checked'
                                    })(<Switch required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])}/>)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_PLAN]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button id="deletePlan" className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!plan} onClick={plan && this.showDeleteConfirm(plan.id)} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row >
        )
    }
}

PlanForm.propTypes = {
    fetching: PropTypes.bool,
    loading: PropTypes.bool,
    apiSource: PropTypes.array.isRequired
}

PlanForm.defaultProps = {
    fetching: false,
    loading: false,
    apiSource: []
}

export default Form.create({})(PlanForm)