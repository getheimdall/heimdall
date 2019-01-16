import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { Row, Form, Col, Input, Transfer } from 'antd'

import i18n from "../../i18n/i18n"
import Loading from '../ui/Loading'
import { operationService } from '../../services'
import { PrivilegeUtils } from "../../utils/PrivilegeUtils"
import { getScope, clearScope } from '../../actions/scopes'
import { privileges } from "../../constants/privileges-types"

const FormItem = Form.Item

class ScopeForm extends Component {

    state = {
        transferLoading: false,
        transferDataSource: [],
        transferSelected: []
    }

    componentDidMount() {
        this.mountTransfer()
        if (this.props.scopeId !== 0) {
            this.setState({ ...this.state, transferLoading: true })
            this.props.dispatch(getScope(this.props.idApi, this.props.scopeId))
        }

        this.props.onRef(this)
    }

    componentDidUpdate(prevProps) {
        if (this.props.scope !== prevProps.scope) {
            this.mountTransferSelected()
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearScope())
        this.props.onRef(undefined)
    }

    mountTransfer = () => {
        let transferDataSource = []

        operationService.getOperationsByApi(this.props.idApi, this.props.idResource)
            .then(data => {
                transferDataSource = data.map(p => {
                    return {
                        key: p.id,
                        title: p.method,
                        description: p.path,
                        disabled: !PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_SCOPE, privileges.PRIVILEGE_UPDATE_SCOPE])
                    }
                })
                this.setState({ ...this.state, transferDataSource: transferDataSource, transferLoading: false })
            })
    }

    mountTransferSelected = () => {
        let transferSelected

        if (this.props.scope && this.props.scope.operations) {
            transferSelected = this.props.scope.operations.map(p => p.id)
        }

        this.setState({ ...this.state, transferSelected: transferSelected })
    }

    filterOption = (inputValue, option) => {
        return option.title.toUpperCase().includes(inputValue.toUpperCase())
    }

    handleChangeTransfer = (targetKeys) => {
        this.setState({ ...this.state, transferSelected: targetKeys })
    }

    onSubmitForm() {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.operations = this.state.transferSelected.map(p => {
                    return { id: p }
                })

                this.props.onSubmit(payload)
            }
        });
    }

    render() {
        if (this.props.formLoading) {
            return <Loading />
        }

        const { getFieldDecorator } = this.props.form
        const { scope } = this.props

        return (
            <Row>
                <Form>
                    {scope && getFieldDecorator('id', { initialValue: scope.id })(<Input type='hidden' />)}
                    <Row type="flex" justify="center">
                        <Col sm={24}>
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: scope && scope.name,
                                        rules: [{ required: true, message: i18n.t('please_input_api_name') }]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_RESOURCE, privileges.PRIVILEGE_UPDATE_RESOURCE])} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: scope && scope.description,
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_RESOURCE, privileges.PRIVILEGE_UPDATE_RESOURCE])} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24}>
                            {
                                this.state.transferLoading ? <Loading /> :
                                    <FormItem>
                                        <Transfer
                                            showSearch
                                            titles={[i18n.t('available_operations'), i18n.t('attributed_operations')]}
                                            onChange={this.handleChangeTransfer}
                                            filterOption={this.filterOption}
                                            dataSource={this.state.transferDataSource}
                                            listStyle={{ width: '300px', height: '300px' }}
                                            targetKeys={this.state.transferSelected}
                                            render={i => (
                                                <span className="custom-item">
                                                    {i.title} - {i.description}
                                                </span>)
                                            }
                                        />
                                    </FormItem>
                            }
                        </Col>
                    </Row>
                </Form>
            </Row>
        )
    }
}

ScopeForm.propTypes = {
    idApi: PropTypes.number.isRequired,
    operations: PropTypes.array
}

ScopeForm.defaultProps = {
    operations: []
}

const mapStateToProps = state => {
    return {
        scope: state.scopes.scope,
        loading: state.scopes.formLoading
    }
}

const WrappedScopeForm = Form.create({})(ScopeForm)

export default connect(mapStateToProps)(WrappedScopeForm)