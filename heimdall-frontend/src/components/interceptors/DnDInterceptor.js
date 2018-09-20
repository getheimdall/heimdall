import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {DragSource} from 'react-dnd'
import ItemTypes from '../../constants/items-types'

import {Button, Badge, Modal, Icon, Popover} from 'antd'
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
            component.setState({...component.state, showModal: true, executionPoint: dropResult.executionPoint})
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

    state = {showModal: false, executionPoint: '', hovered: false, clicked: false}

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
            hovered: false,
        });
    }

    handleHoverChange = (visible) => {
        this.setState({
            ...this.state,
            hovered: visible,
            clicked: false,
        });
    }

    handleClickChange = (visible) => {
        this.setState({
            ...this.state,
            clicked: visible,
            hovered: false,
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
        this.props.handleDelete(this.props.interceptor);
    }

    render() {
        const {type, interceptor, color, icon, isDragging, connectDragSource} = this.props
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

        const hoverContent = (
            <div>
                {
                    interceptor && interceptor.description &&
                    (<span><b>Description: </b> {interceptor.description}<br/></span>)
                }

                {
                    interceptor && interceptor.lifeCycle &&
                    (<span><b>Life Cycle: </b> {interceptor.lifeCycle}<br/></span>)
                }
                <br/>
                <span>Click in circle to edit or delete.</span>
            </div>
        )

        const clickContent = (
            <div className="heimdall-interceptor-popover-actions">
                <Button onClick={this.showModal}><Icon type="edit" theme="outlined" /></Button>
                <Button onClick={this.handleRemoveInterceptor}><Icon type="delete" theme="outlined" /></Button>
            </div>
        )

        return (
            connectDragSource(
                <div className="draggable-interceptor">
                    <Badge count={interceptor && interceptor.order} showZero
                           style={{background: '#ada56e3b', color: '#000'}}>
                        <Popover content={hoverContent} title={interceptor && `Name: ${interceptor.name}`} trigger="hover"
                                 visible={this.state.hovered} onVisibleChange={this.handleHoverChange}>
                            <Popover content={clickContent} title={interceptor && `Name: ${interceptor.name}`} trigger="click"
                                 visible={this.state.clicked} onVisibleChange={this.handleClickChange}>
                                <div className="ant-btn ant-btn-circle ant-btn-lg ant-btn-icon-only" style={style}>
                                    <Icon type={icon}/>
                                </div>
                            </Popover>
                        </Popover>
                    </Badge>
                    <span>{type}</span>

                    <Modal title="Add Resource"
                           footer={[
                               <Button id="cancelInterceptorModal" key="back"
                                       onClick={this.handleCancel}>Cancel</Button>,
                               <Button id="saveInterceptorModal" key="submit" type="primary" onClick={this.handleSave}>
                                   Save
                               </Button>
                           ]}
                           visible={this.state.showModal}
                           onCancel={this.handleCancel}
                           destroyOnClose>
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