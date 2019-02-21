import flow from 'lodash.flow'
import PropTypes from 'prop-types'
import React, {Component} from 'react'
import {DragSource, DropTarget} from 'react-dnd'
import { Button, Modal, Icon, Popover} from 'antd'

import i18n from "../../i18n/i18n"
import InterceptorForm from './InterceptorForm'
import ItemTypes from '../../constants/items-types'
import ComponentAuthority from "../policy/ComponentAuthority"
import {privileges} from "../../constants/privileges-types"

const interceptorSpec = {
    beginDrag(props) {
        return {
            name: props.interceptor.name,
            order: props.interceptor.order,
            executionPoint: props.interceptor.executionPoint,
            lifeCycle: props.interceptor.lifeCycle,
            referenceId: props.interceptor.referenceId,
            id: props.interceptor.id,
            uuid: props.interceptor.uuid
        }
    },
    endDrag(props, monitor, component) {
        if (!component){
            return
        }
        const dropResult = monitor.getDropResult()
        const didDrop = monitor.didDrop()
        if (didDrop) {
            const interceptor = component.props.interceptor
            if (interceptor.executionPoint !== dropResult.executionPoint) {
                interceptor.executionPoint = dropResult.executionPoint
                interceptor.order = dropResult.sizeInterceptors
                props.handleForm(interceptor)
            }
        } else {
            props.handleDelete(props.interceptor)
        }
    }
}

const interceptorTarget = {
    drop(props, monitor, component) {
        if (!component) {
            return
        }

        const idHover = props.interceptor.id
        const lifeCycleHover = props.interceptor.lifeCycle
        const referenceIdHover = props.interceptor.referenceId
        const uuidHover = props.interceptor.uuid

        const idDrag = monitor.getItem().id
        const lifeCycleDrag = monitor.getItem().lifeCycle
        const referenceDrag = monitor.getItem().referenceId
        const uuidDrag = monitor.getItem().uuid

        const dragReference = idDrag ? idDrag : uuidDrag
        const hoverReference = idHover ? idHover : uuidHover

        if ((idHover !== idDrag || uuidHover !== uuidDrag ) && lifeCycleHover ===  lifeCycleDrag && referenceIdHover === referenceDrag) {
            props.moveInterceptors(dragReference, hoverReference, lifeCycleDrag, referenceDrag)
        }
    },
    canDrop(props, monitor) {
        const idHover = props.interceptor.id
        const lifeCycleHover = props.interceptor.lifeCycle
        const referenceIdHover = props.interceptor.referenceId
        const executionPointHover = props.interceptor.executionPoint

        const idDrag = monitor.getItem().id
        const lifeCycleDrag = monitor.getItem().lifeCycle
        const referenceDrag = monitor.getItem().referenceId
        const executionPointDrag = monitor.getItem().executionPoint

        if (!idDrag) {
            return lifeCycleDrag === lifeCycleHover && executionPointDrag === executionPointHover
        }

        return idHover !== idDrag && lifeCycleHover ===  lifeCycleDrag && referenceIdHover === referenceDrag && executionPointDrag === executionPointHover
    }
}

let connect = (connect, monitor) => {
    return {
        connectDropTarget: connect.dropTarget(),
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop()
    }
}

let collect = (connect, monitor) => {
    return {
        connectDragSource: connect.dragSource(),
        isDragging: monitor.isDragging()
    }
}

class DnDInterceptor extends Component {

    state = {showModal: false, executionPoint: '', clicked: false}

    handleSave = (e) => {
        this.interceptorForm.onSubmitForm() //calling interceptor form submit
    }

    closeModal = () => {
        this.setState({...this.state, showModal: false})
    }

    handleCancel = (e) => {
        this.setState({...this.state, showModal: false});
    }

    hide = () => {
        this.setState({
            ...this.state,
            clicked: false,
        });
    }

    handleClickChange = (visible) => {
        this.setState({
            ...this.state,
            clicked: visible,
        });
    }

    showModal = () => {
        this.setState({
            ...this.state,
            clicked: false,
            hovered: false,
            showModal: true,
        })
    }

    handleRemoveInterceptor = () => {
        this.hide();
        this.props.handleDelete(this.props.interceptor);
    }

    render() {
        const {type, interceptor, color, icon, isDragging, connectDragSource, connectDropTarget, isOver, canDrop} = this.props
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

        let styledIsOver = {}

        if (canDrop && !isOver) {
            styledIsOver = {
                background: '#64befc'
            }
        }

        if (isOver && canDrop) {
            styledIsOver = {
                background: '#58fc9f'
            }
        }

        if (!canDrop && isOver) {
            styledIsOver = {
                background: '#fc474c'
            }
        }

        const resumeInterceptor = (
            <div>
                {
                    interceptor && interceptor.description &&
                    (<span><b>{i18n.t('description')}: </b> {interceptor.description}<br/></span>)
                }

                {
                    interceptor && interceptor.lifeCycle &&
                    (<span><b>{i18n.t('life_cycle')}: </b> {interceptor.lifeCycle}<br/></span>)
                }
                <br/>
            </div>
        )

        const clickContent = (
            <div className="heimdall-interceptor-popover-actions">
                {resumeInterceptor}
                <Button onClick={this.showModal}><Icon type="edit" theme="outlined"/></Button>
                <Button onClick={this.handleRemoveInterceptor}><Icon type="delete" theme="outlined"/></Button>
            </div>
        )

        return (
            connectDragSource(
                connectDropTarget(
                    <div className="draggable-interceptor"  style={styledIsOver}>
                        <Popover content={clickContent} title={interceptor && `${i18n.t('name')}: ${interceptor.name}`}
                                 trigger="click"
                                 visible={this.state.clicked} onVisibleChange={this.handleClickChange}>
                            <div className="ant-btn ant-btn-circle ant-btn-lg ant-btn-icon-only" style={style}>
                                <Icon type={icon}/>
                            </div>
                        </Popover>
                        <span>{type}</span>

                        <Modal title={i18n.t('edit_interceptor')}
                            footer={[
                                <Button id="cancelInterceptorModal" key="back" onClick={this.handleCancel}>{i18n.t('cancel')}</Button>,
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_INTERCEPTOR, privileges.PRIVILEGE_UPDATE_INTERCEPTOR]}>
                                    <Button id="saveInterceptorModal" key="submit" type="primary" onClick={this.handleSave}>
                                        {i18n.t('save')}
                                    </Button>
                                </ComponentAuthority>
                            ]}
                            visible={this.state.showModal}
                            onCancel={this.handleCancel}
                            destroyOnClose >
                            <InterceptorForm
                                onRef={ref => (this.interceptorForm = ref)}
                                interceptor={interceptor}
                                type={type}
                                order={interceptor.order}
                                executionPoint={interceptor.executionPoint}
                                handleForm={this.props.handleForm}
                                closeModal={this.closeModal}
                            />
                        </Modal>
                    </div>
                )
            )
        )
    }
}

DnDInterceptor.propTypes = {
    name: PropTypes.string,
    icon: PropTypes.string,
    connectDragSource: PropTypes.func.isRequired,
    isDragging: PropTypes.bool.isRequired,
    moveInterceptors: PropTypes.func.isRequired,
    order: PropTypes.number.isRequired,
}

export default flow(DropTarget(ItemTypes.INTERCEPTORS, interceptorTarget, connect), DragSource(ItemTypes.INTERCEPTORS, interceptorSpec, collect))(DnDInterceptor)