import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { DragSource } from 'react-dnd'
import { Button, Badge, Modal, Icon } from 'antd'

import i18n from "../../i18n/i18n";
import ItemTypes from '../../constants/items-types'
import InterceptorForm from './InterceptorForm'

const interceptorSpec = {
    beginDrag(props) {
        return {
            name: props.name,
            operationId: props.operationId,
            resourceId: props.resourceId,
            planId: props.planId,
            environmentId: props.environmentId,
        }
    },
    endDrag(props, monitor, component) {
        const dropResult = monitor.getDropResult()
        const didDrop = monitor.didDrop()
        if (didDrop) {
            component.setState({ ...component.state, showModal: true, executionPoint: dropResult.executionPoint, order: dropResult.sizeInterceptors })
        }
    },
    canDrag(props) {
        return !!props.canAddInterceptor;
    }
}

let collect = (connect, monitor) => {
    return {
        connectDragSource: connect.dragSource(),
        isDragging: monitor.isDragging()
    }
}

class DnDInterceptorType extends Component {

    state = { showModal: false, executionPoint: '', order: 0 }

    handleSave = (e) => {
        this.interceptorForm.onSubmitForm()
    }

    closeModal = () => {
        this.setState({ ...this.state, showModal: false })
    }

    handleCancel = (e) => {
        this.setState({ ...this.state, showModal: false });
    }

    render() {
        const { type, interceptor, color, icon, isDragging, connectDragSource } = this.props
        const opacity = isDragging ? 0.4 : 1
        const style = {
            margin: '0 5px',
            opacity: opacity,
            cursor: 'move',
            marginBottom: 5,
            padding: 5,
            backgroundColor: color
        }

        return (
            connectDragSource(
                <div className="draggable-interceptor">
                    <Badge count={interceptor && interceptor.order} showZero style={{ background: '#ada56e3b', color: '#000' }}>
                        <div
                            className="ant-btn ant-btn-circle ant-btn-lg ant-btn-icon-only"
                            style={style}
                        >
                            <Icon type={icon} />
                        </div>
                    </Badge>
                    <span>{type}</span>

                    <Modal title={i18n.t('add_interceptor')}
                        footer={[
                            <Button id="cancelInterceptorTypeModal" key="back" onClick={this.handleCancel}>{i18n.t('cancel')}</Button>,
                            <Button id="saveInterceptorTypeModal" key="submit" type="primary" onClick={this.handleSave}>
                                {i18n.t('save')}
                            </Button>
                        ]}
                        visible={this.state.showModal}
                        onCancel={this.handleCancel}
                        destroyOnClose >
                        <InterceptorForm
                            onRef={ref => (this.interceptorForm = ref)}
                            interceptor={interceptor}
                            environmentId={this.props.environmentId !== 0 && this.props.environmentId}
                            apiId={this.props.apiId !== 0 && this.props.apiId}
                            planId={this.props.planId !== 0 && this.props.planId}
                            resourceId={this.props.resourceId !== 0 && this.props.resourceId}
                            operationId={this.props.operationId !== 0 && this.props.operationId}
                            executionPoint={this.state.executionPoint}
                            type={type}
                            order={this.state.order}
                            handleForm={this.props.handleForm}
                            closeModal={this.closeModal}
                        />
                    </Modal>
                </div>
            )
        )
    }
}

DnDInterceptorType.propTypes = {
    name: PropTypes.string,
    icon: PropTypes.string,
    connectDragSource: PropTypes.func.isRequired,
    isDragging: PropTypes.bool.isRequired
}

export default DragSource(ItemTypes.INTERCEPTORS, interceptorSpec, collect)(DnDInterceptorType)