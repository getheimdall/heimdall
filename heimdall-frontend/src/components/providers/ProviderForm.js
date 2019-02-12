import React from 'react'
import { Button, Col, Form, Input, List, Modal, Row, Tooltip, Divider } from "antd"

import i18n from "../../i18n/i18n"
import ProviderParamForm from "./ProviderParamForm"
import ComponentAuthority from "../policy/ComponentAuthority"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"

const FormItem = Form.Item
const confirm = Modal.confirm
const ButtonGroup = Button.Group

class ProviderForm extends React.Component {

    constructor(props) {
        super(props)
        this.state = {providerParams: [], providerParamSelected: 0, visibleModal: false}
    }

    componentDidMount() {

        const { provider } = this.props

        if (provider && provider.providerParams) {
            this.setState({ ...this.state, providerParams: provider.providerParams})
        }
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.providerParams = this.state.providerParams
                this.props.handleSubmit(payload)
            }
        })
    }

    showDeleteConfirm = (providerId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(providerId)
            }
        });
    }

    handleSave = (e) => {
        this.providerParamForm.onSubmitForm()
    }

    showOperationModal = (providerParamId) => (e) => {

        let newProviderParamId = this.state.providerParamSelected;
        if (providerParamId) {
            newProviderParamId = providerParamId
        }
        this.setState({ ...this.state, providerParamSelected: newProviderParamId, visibleModal: true })
    }

    handleCancel = (e) => {
        this.setState({ ...this.state, operationSelected: 0, visibleModal: false });
    }

    submitPayload = providerParam => {

        if (providerParam) {
            const { providerParams } = this.state

            let providersParamsUpdated = []

            if (providerParam.id) {
                providersParamsUpdated = providerParams.filter(p => providerParam.id !== p.id)
            } else {
                providersParamsUpdated = providerParams.filter(p => providerParam.uuid !== p.uuid)
            }

            providersParamsUpdated.push(providerParam)
            this.setState({ ...this.state, providerParams: providersParamsUpdated, providerParamSelected: 0, visibleModal: false })
        } else {
            this.setState({ ...this.state, providerParamSelected: 0, visibleModal: false })
        }
    }

    removeProviderParam = providerParamId => {
        const { providerParams } = this.state
        //
        let providersParamsUpdated = providerParams.filter(p => p.id !== providerParamId && p.uuid !== providerParamId)
        this.setState({ ...this.state, providerParams: providersParamsUpdated })
    }


    render() {

        const { providerParams } = this.state
        const {getFieldDecorator} = this.props.form
        const {provider, loading} = this.props

        const modalProviderParam = (
            <Modal title={i18n.t('add_provider_param')}
                   footer={[
                       <Button id="cancelAddProviderParam" key="back"
                               onClick={this.handleCancel}>{i18n.t('cancel')}</Button>,
                       <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_OPERATION, privileges.PRIVILEGE_UPDATE_OPERATION]} key={1}>
                           <Button id="saveProviderParam" key="submit" type="primary" loading={loading}
                                   onClick={this.handleSave}>{i18n.t('save')}</Button>
                       </ComponentAuthority>
                   ]}
                   visible={this.state.visibleModal}
                   onCancel={this.handleCancel}
                   destroyOnClose>
                <ProviderParamForm onRef={ref => (this.providerParamForm = ref)} onSubmit={this.submitPayload} providerParams={providerParams} providerParamId={this.state.providerParamSelected}/>
            </Modal>
        )

        return (
            <Row>
                <Form>
                    {provider && getFieldDecorator('id', {initialValue: provider.id})(<Input type='hidden'/>)}
                    <Row gutter={24}>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: provider && provider.name,
                                        rules: [
                                            {required: true, message: i18n.t('please_input_provider_name')},
                                            {min: 4, message: i18n.t('min_5_characters_to_name')}
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('path')}>
                                {
                                    getFieldDecorator('path', {
                                        initialValue: provider && provider.path,
                                        rules: [
                                            {required: true, message: i18n.t('please_input_provider_description')}
                                        ]
                                    })(<Input
                                        disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: provider && provider.description,
                                        rules: [
                                            {required: true, message: i18n.t('please_input_provider_description')}
                                        ]
                                    })(<Input
                                        disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            {
                                providerParams && providerParams.length === 0 &&
                                (
                                    <Row type="flex" justify="center" align="bottom">
                                        {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER]) &&
                                        <Col style={{ marginTop: 20 }}>
                                            {i18n.t('you_do_not_have')} <b style={{textTransform: 'uppercase'}}>{i18n.t('provider_params')}</b> {i18n.t('in_this')} <b style={{textTransform: 'uppercase'}}>{i18n.t('provider')}</b>! <Button id="addOperationWhenListIsEmpty" type="dashed" onClick={this.showOperationModal()}>{i18n.t('add_provider_param')}</Button>
                                        </Col>
                                        }
                                        {!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER]) &&
                                        <Col style={{ marginTop: 20 }}>
                                            {i18n.t('you_do_not_have')} <b style={{textTransform: 'uppercase'}}>{i18n.t('provider_params')}</b> {i18n.t('in_this')} <b style={{textTransform: 'uppercase'}}>{i18n.t('provider')}</b>!
                                        </Col>
                                        }

                                        {modalProviderParam}
                                    </Row>
                                )
                            }
                            {
                                providerParams && providerParams.length > 0 && PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_PROVIDER]) &&
                                (
                                    <Row type="flex" justify="center" align="bottom">
                                        <Col style={{ marginTop: 20 }}>
                                            <Button id="addOperationWhenListIsEmpty" type="dashed" onClick={this.showOperationModal()}>{i18n.t('add_provider_param')}</Button>
                                        </Col>
                                    </Row>
                                )
                            }
                        </Col>
                        <Col sm={24} md={24}>
                            {
                                providerParams.length > 0 && PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_PROVIDER]) &&
                                <List
                                    itemLayout="horizontal"
                                    dataSource={providerParams}
                                    renderItem={providerParam => {

                                        return (
                                            <List.Item>
                                                <List.Item.Meta
                                                    title={`${i18n.t('name')}: ${providerParam.name}`}
                                                    description={`${i18n.t('location')}: ${providerParam.location}`}
                                                />
                                                <Row type="flex" justify="center">
                                                    <ButtonGroup>
                                                        <Tooltip title={i18n.t('edit')}>
                                                            <Button type="primary" icon="edit" onClick={this.showOperationModal(providerParam.id ? providerParam.id : providerParam.uuid)} />
                                                        </Tooltip>
                                                        <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_PROVIDER]}>
                                                            <Tooltip title={i18n.t('delete')}>
                                                                <Button type="danger" icon="delete" onClick={() => this.removeProviderParam(providerParam.id ? providerParam.id : providerParam.uuid)} />
                                                            </Tooltip>
                                                        </ComponentAuthority>
                                                    </ButtonGroup>
                                                </Row>
                                            </List.Item>
                                        )
                                    }}
                                />
                            }
                            { providerParams.length > 0 && PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_PROVIDER]) && modalProviderParam }
                        </Col>
                    </Row>
                </Form>
                <Divider type="horizontal" />
                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_PROVIDER]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button id="deletePlan" className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!provider} onClick={provider && this.showDeleteConfirm(provider.id)} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_PROVIDER, privileges.PRIVILEGE_UPDATE_PROVIDER]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row>
        )
    }
}

export default Form.create({})(ProviderForm)