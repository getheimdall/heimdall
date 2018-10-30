import React, { Component } from 'react'
import { connect } from 'react-redux'
import PropTypes from 'prop-types'
import { List, Avatar, Button, Row, Col, Tooltip, Modal, notification } from 'antd'

import i18n from '../i18n/i18n'
import { operationService } from '../services'
import Loading from '../components/ui/Loading'
import FloatSearch from './../components/ui/FloatSearch'
import OperationForm from '../components/operations/OperationForm'

const ButtonGroup = Button.Group;

class Operations extends Component {

    constructor(props) {
        super(props)
        this.state = { operations: null, operationSelected: 0, visibleModal: false, operationsFiltered: null, visibleSearch: false }
    }

    componentDidMount() {
        this.reloadOperations()
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    showOperationModal = (operationId) => (e) => {
        let newOperationId = this.state.operationSelected;
        if (operationId) {
            newOperationId = operationId
        }
        this.setState({ ...this.state, operationSelected: newOperationId, visibleModal: true });
    }

    handleCancel = (e) => {
        this.setState({ ...this.state, operationSelected: 0, visibleModal: false });
    }

    reloadOperations = () => {
        this.setState({ ...this.state, operations: null });
        operationService.getOperationsByResource(this.props.idApi, this.props.idResource)
            .then(data => {
                this.setState({ ...this.state, operations: data, operationsFiltered: data});
            })
    }

    saveOperation = (idApi, idResource, operation) => {
        operationService.save(idApi, idResource, operation)
            .then(data => {
                this.reloadOperations()
            })

    }

    updateOperation = (idApi, idResource, operation) => {
        operationService.update(idApi, idResource, operation)
            .then(data => {
                this.reloadOperations()
            })
    }

    removeOperation = (idApi, idResource, operation) => {
        operationService.remove(idApi, idResource, operation)
            .then(data => {
                this.reloadOperations()
            })
            .catch(error => {
                if (error.response && error.response.status === 400) {
                    notification['error']({ message: i18n.t('error'), description: error.response.data.message })
                }
                this.reloadOperations()
            })
    }

    handleSave = (e) => {
        this.operationForm.onSubmitForm()
        this.setState({ ...this.state, operationSelected: 0, operations: null, visibleModal: false });
    }

    submitPayload = (payload) => {
        if (!payload.id) {
            this.saveOperation(this.props.idApi, this.props.idResource, payload)
        } else {
            this.updateOperation(this.props.idApi, this.props.idResource, payload)
        }
    }

    remove = (operationId) => (e) => {
        this.setState({ ...this.state, operationSelected: 0, operations: null });
        this.removeOperation(this.props.idApi, this.props.idResource, operationId)
    }

    toggleSearch = () => {
        const { visibleSearch } = this.state
        this.setState({ ...this.state, visibleSearch: !visibleSearch })
    }

    filterOperationsByPath = (e) => {
        const value = e.target.value
        const { operations } = this.state
        try {
            const reg = new RegExp(value, 'i')
            this.setState({ ...this.state, operationsFiltered: operations.filter(o => o.path.match(reg) !== null)})
        } catch (e) {
            this.setState({ ...this.state, operationsFiltered: operations })
        }
    }

    render() {
        const { operations, operationsFiltered, visibleSearch } = this.state;
        const { loading } = this.props
        if (!operations) return <Loading />

        const modalOperation =
            <Modal title={i18n.t('add_operation')}
            
                footer={[
                    <Button id="cancelAddOperation" key="back" onClick={this.handleCancel}>{i18n.t('cancel')}</Button>,
                    <Button id="saveOperation" key="submit" type="primary" loading={loading} onClick={this.handleSave}>{i18n.t('save')}</Button>
                ]}
                visible={this.state.visibleModal}
                onCancel={this.handleCancel}
                destroyOnClose >
                <OperationForm onRef={ref => (this.operationForm = ref)} onSubmit={this.submitPayload} operationId={this.state.operationSelected} idApi={this.props.idApi} idResource={this.props.idResource} apiBasepath={this.props.apiBasepath}/>
            </Modal>

        if (operations && operations.length === 0) {
            return (
                <Row type="flex" justify="center" align="bottom">
                    <Col style={{ marginTop: 20 }}>
                        {i18n.t('you_do_not_have')} <b style={{textTransform: 'uppercase'}}>{i18n.t('operations')}</b> {i18n.t('in_this')} <b style={{textTransform: 'uppercase'}}>{i18n.t('resource')}</b>! <Button id="addOperationWhenListIsEmpty" type="dashed" onClick={this.showOperationModal()}>{i18n.t('add_operation')}</Button>
                    </Col>

                    {modalOperation}
                </Row>
            )
        }

        return (
            <div style={{ position: 'relative' }}>
                { visibleSearch && <FloatSearch callbackKeyUp={this.filterOperationsByPath} handleClose={this.toggleSearch}/>}
                <Row type="flex" justify="center">
                    <Col style={{margin: 5}}>
                        <Tooltip title={i18n.t('add_operation')}>
                            <Button id="addOperation" type="dashed" icon="plus" onClick={this.showOperationModal()}>{i18n.t('add_operation')}</Button>
                        </Tooltip>
                    </Col>
                    <Col style={{margin: 5}}>
                        <Tooltip title={i18n.t('search_operations')}>
                            <Button id="searchOperations" type="dashed" icon="search" onClick={this.toggleSearch}>{i18n.t('search_operations')}</Button>
                        </Tooltip>
                    </Col>
                </Row>
                {/* <hr /> */}
                <List
                    className="demo-loadmore-list"
                    itemLayout="horizontal"
                    dataSource={operationsFiltered}
                    renderItem={operation => {
                        let color;
                        if (operation.method === 'GET') {
                            color = '#61affe'
                        } else if (operation.method === 'POST') {
                            color = '#49cc90'
                        } else if (operation.method === 'DELETE') {
                            color = '#f93e3e'
                        } else if (operation.method === 'PUT') {
                            color = '#fca130'
                        } else if (operation.method === 'PATCH') {
                            color = '#50e3c2'
                        }

                        let description = (operation.description === 'null' || operation.description === null) ? '' : operation.description

                        return (
                            <List.Item>
                                <List.Item.Meta
                                    avatar={
                                        <Avatar style={{ backgroundColor: color, verticalAlign: 'middle' }} size="large">
                                            {operation.method}
                                        </Avatar>
                                    }
                                    title={operation.path}
                                    description={description}
                                />
                                <Row type="flex" justify="center">
                                    <ButtonGroup>
                                        <Tooltip title={i18n.t('edit')}>
                                            <Button type="primary" icon="edit" onClick={this.showOperationModal(operation.id)} />
                                        </Tooltip>
                                        <Tooltip title={i18n.t('delete')}>
                                            <Button type="danger" icon="delete" onClick={this.remove(operation.id)} />
                                        </Tooltip>
                                    </ButtonGroup>
                                </Row>
                            </List.Item>
                        )
                    }}
                />
                {modalOperation}
            </div>
        )
    }

}

Operations.propType = {
    idApi: PropTypes.number.isRequired,
    idResource: PropTypes.number.isRequired,
    apiBasepath: PropTypes.number.isRequired
}

const mapStateToProps = state => {
    return {
        loading: state.operations.loading,
        reload: state.operations.reload
    }
}

export default connect(mapStateToProps)(Operations)