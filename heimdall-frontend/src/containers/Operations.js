import React, { Component } from 'react'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import PropTypes from 'prop-types'
import { operationService } from '../services'
import { toggleModal } from '../actions/operations'

import { List, Avatar, Button, Row, Col, Tooltip, Modal, notification } from 'antd';
import Loading from '../components/ui/Loading'
import OperationForm from '../components/operations/OperationForm';

const ButtonGroup = Button.Group;

class Operations extends Component {

    constructor(props) {
        super(props)
        this.state = { operations: null, operationSelected: 0 }
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
        if (operationId) {
            this.setState({ ...this.state, operationSelected: operationId });
        }
        this.props.toggleModal(true)
    }

    handleCancel = (e) => {
        this.props.toggleModal(false)
        this.setState({ ...this.state, operationSelected: 0 });
    }

    reloadOperations = () => {
        this.setState({ ...this.state, operations: null });
        operationService.getOperationsByResource(this.props.idApi, this.props.idResource)
            .then(data => {
                this.setState({ ...this.state, operations: data });
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
                    notification['error']({ message: 'Error', description: error.response.data.message })
                }
                this.reloadOperations()
            })
    }

    handleSave = (e) => {
        this.operationForm.onSubmitForm()
        this.setState({ ...this.state, operationSelected: 0, operations: null });
        this.props.toggleModal(false)
    }

    submitPayload = (payload) => {
        console.log(payload)
        if (!payload.id) {
            this.saveOperation(this.props.idApi, this.props.idResource, payload)
        } else {
            this.updateOperation(this.props.idApi, this.props.idResource, payload)
        }
    }

    remove = (operationId) => (e) => {
        console.log(this.props.idApi, this.props.idResource, operationId)
        this.setState({ ...this.state, operationSelected: 0, operations: null });
        this.removeOperation(this.props.idApi, this.props.idResource, operationId)
        // this.reloadOperations()
    }

    render() {
        const { operations } = this.state;
        const { loading } = this.props
        if (!operations) return <Loading />

        const modalOperation =
            <Modal title="Add Operation"
                footer={[
                    <Button key="back" onClick={this.handleCancel}>Cancel</Button>,
                    <Button key="submit" type="primary" loading={loading} onClick={this.handleSave}>Save</Button>
                ]}
                visible={this.props.visibleModal}
                onCancel={this.handleCancel}
                destroyOnClose >
                <OperationForm onRef={ref => (this.operationForm = ref)} onSubmit={this.submitPayload} operationId={this.state.operationSelected} idApi={this.props.idApi} idResource={this.props.idResource} />
            </Modal>

        if (operations && operations.length === 0) {
            return (
                <Row type="flex" justify="center" align="bottom">
                    <Col style={{ marginTop: 20 }}>
                        You don't have <b>OPERATIONS</b> in this <b>RESOURCE</b>, please <Button type="dashed" onClick={this.showOperationModal()}>Add Operation</Button>
                    </Col>

                    {modalOperation}
                </Row>
            )
        }

        return (
            <div>
                <Row type="flex" justify="center">
                    <Tooltip title="Add Operation">
                        <Button type="dashed" icon="plus" onClick={this.showOperationModal()}>Add Operation</Button>
                    </Tooltip>
                </Row>
                {/* <hr /> */}
                <List
                    className="demo-loadmore-list"
                    itemLayout="horizontal"
                    dataSource={operations}
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
                                        <Tooltip title="Update">
                                            <Button type="primary" icon="edit" onClick={this.showOperationModal(operation.id)} />
                                        </Tooltip>
                                        <Tooltip title="Delete">
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
    idResource: PropTypes.number.isRequired
}

const mapStateToProps = state => {
    return {
        visibleModal: state.operations.visibleModal,
        loading: state.operations.loading,
        reload: state.operations.reload
    }
}

const mapDispatchToProps = dispatch => {
    return {
        toggleModal: bindActionCreators(toggleModal, dispatch)
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Operations)