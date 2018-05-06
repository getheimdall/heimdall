import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { DragSource } from 'react-dnd'
import ItemTypes from '../../constants/items-types'

import { Button, Badge, Modal, Tooltip, Icon } from 'antd'
import InterceptorForm from './InterceptorForm'

const interceptorSpec = {
    beginDrag(props) {
        return {
            name: props.name
        }
    },
    endDrag(props, monitor, component) {
        // const dragItem = monitor.getItem()
        const dropResult = monitor.getDropResult()
        const didDrop = monitor.didDrop()
        if (didDrop) {
            component.setState({ ...component.state, showModal: true, executionPoint: dropResult.executionPoint })
        } else {
            props.handleDelete(props.interceptor)
        }
    }
}

let collect = (connect, monitor) => {
    return {
        connectDragSource: connect.dragSource(),
        isDragging: monitor.isDragging()
    }
}

class DnDInterceptor extends Component {

    state = { showModal: false, executionPoint: '' }

    handleSave = (e) => {
        this.interceptorForm.onSubmitForm() //calling interceptor form submit
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
        const borderColor = interceptor.status && 'solid 1px #000000'
        const style = {
            margin: '0 5px',
            opacity: opacity,
            cursor: 'move',
            marginBottom: 5,
            padding: 5,
            backgroundColor: color,
            border: borderColor
        }

        return (
            connectDragSource(
                <div className="draggable-interceptor">
                    <Tooltip title='Drag out to remove'>
                        <Badge count={interceptor && interceptor.order} showZero style={{ background: '#ada56e3b', color: '#000' }}>
                            <div className="ant-btn ant-btn-circle ant-btn-lg ant-btn-icon-only" style={style}>
                                <Icon type={icon} />
                            </div>
                        </Badge>
                    </Tooltip>
                    <span>{type}</span>

                    <Modal title="Add Resource"
                        footer={[
                            <Button key="back" onClick={this.handleCancel}>Cancel</Button>,
                            <Button key="submit" type="primary" onClick={this.handleSave}>
                                Save
                            </Button>
                        ]}
                        visible={this.state.showModal}
                        onCancel={this.handleCancel}
                        destroyOnClose >
                        <InterceptorForm
                            onRef={ref => (this.interceptorForm = ref)}
                            interceptor={interceptor}
                            type={type}
                            executionPoint={this.state.executionPoint}
                            handleForm={this.props.handleForm}
                            closeModal={this.closeModal}
                        />
                    </Modal>
                </div>
            )
        )
    }
}

DnDInterceptor.propTypes = {
    name: PropTypes.string,
    icon: PropTypes.string,
    connectDragSource: PropTypes.func.isRequired,
    isDragging: PropTypes.bool.isRequired
}

export default DragSource(ItemTypes.INTERCEPTORS, interceptorSpec, collect)(DnDInterceptor)