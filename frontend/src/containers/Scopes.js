import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Button, notification, Row, Tooltip, Modal, Col } from 'antd'

import i18n from "../i18n/i18n"
import ScopeForm from "../components/scopes/ScopeForm"
import ListScopes from "../components/scopes/ListScopes"
import { privileges } from "../constants/privileges-types"
import ComponentAuthority from "../components/policy/ComponentAuthority"
import { getScopes, initLoading, save, clearScope, update, remove } from '../actions/scopes'

class Scopes extends Component {

    state = {
        page: 0,
        pageSize: 10,
        scopeSelected: 0,
        visibleModal: false
    }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getScopes(this.props.api.id, { offset: 0, limit: 10 }))
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getScopes(this.props.api.id, { offset: page - 1, limit: 10 }))
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(clearScope())
        if (formObject.id) {
            this.props.dispatch(update(this.props.api.id, formObject))
        } else {
            this.props.dispatch(save(this.props.api.id, formObject))
        }
        this.setState({ ...this.state, scopeSelected: 0 })
    }

    handleDelete = (objectId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(this.props.api.id, objectId))
    }

    handleSave = () => {
        this.scopeForm.onSubmitForm()
        this.setState({ ...this.state, scopeSelected: 0, visibleModal: false })
    }

    handleCancel = () => {
        this.setState({ ...this.state, scopeSelected: 0, visibleModal: false })
    }

    showScopeModal = (formId) => () => {
        let newScopeSelected = this.state.scopeSelected;
        if (formId) {
            newScopeSelected = formId
        }
        this.setState({ ...this.state, scopeSelected: newScopeSelected, visibleModal: true })
    }

    render() {

        const { loading, scopes } = this.props

        let title = 'add_scope'

        if (this.state.scopeSelected !== 0) {
            title = 'edit_scope'
        }

        const modalScope =
            <Modal title={i18n.t(title)}
                footer={[
                    <Button id="cancelAddScope" key="back" onClick={this.handleCancel}>{i18n.t('cancel')}</Button>,
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_SCOPE, privileges.PRIVILEGE_UPDATE_SCOPE]} key={1}>
                        <Button id="saveScope" key="submit" type="primary" loading={loading} onClick={this.handleSave}>{i18n.t('save')}</Button>
                    </ComponentAuthority>
                ]}
                width={700}
                visible={this.state.visibleModal}
                onCancel={this.handleCancel}
                destroyOnClose >
                <ScopeForm onRef={ref => (this.scopeForm = ref)} onSubmit={this.handleSubmit} scopeId={this.state.scopeSelected} idApi={this.props.api.id} />
            </Modal>

        if (scopes && scopes.length === 0) {
            return (
                <Row type="flex" justify="center" align="bottom">
                    <Col style={{ marginTop: 20 }}>
                        {i18n.t('you_do_not_have_resources_in_this')} <b>{i18n.t('scope')}</b>!
                        <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_SCOPE]}>
                            <Button id="addScopeWhenListIsEmpty" type="dashed" className="add-tour" onClick={this.showScopeModal()}>
                                {i18n.t('add_scope')}
                            </Button>
                        </ComponentAuthority>
                    </Col>

                    {modalScope}
                </Row>
            )
        }

        return (
            <React.Fragment>
                <ListScopes dataSource={scopes} handleDelete={this.handleDelete} handlePagination={this.handlePagination} handleEdit={this.showScopeModal} loading={loading} />

                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_SCOPE]}>
                    <Row type="flex" justify="end">
                        <Tooltip title={i18n.t('add_scope')}>
                            <Button id="addScope" className="card-button add-tour" type="primary" icon="plus" onClick={this.showScopeModal()} size="large" shape="circle" />
                        </Tooltip>
                    </Row>
                </ComponentAuthority>
                {modalScope}
            </React.Fragment>
        )
    }
}


const mapStateToProps = state => {
    return {
        scopes: state.scopes.scopes,
        loading: state.scopes.loading,
        notification: state.scopes.notification
    }
}

export default connect(mapStateToProps)(Scopes)