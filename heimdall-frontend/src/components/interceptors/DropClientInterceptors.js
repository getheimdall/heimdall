import React, { Component } from 'react'
import { DropTarget } from 'react-dnd'
import PropTypes from 'prop-types'
import ItemTypes from '../../constants/items-types'
import { Row } from 'antd'

import DnDInterceptor from './DnDInterceptor'
import Loading from '../ui/Loading';
import {changeOrder} from "../../utils/OrderInterceptorsUtisl";

const ClientInterceptorsSpec = {
    drop(props, monitor) {
        let lifeCycle = ''
        const item = monitor.getItem()
        if (item.apiId) {
            lifeCycle = 'API'
        } else if (item.operationId) {
            lifeCycle = 'OPERATION'
        } else if (item.resourceId) {
            lifeCycle = 'RESOURCE'
        } else if (item.planId) {
            lifeCycle = 'PLAN'
        } else {
            lifeCycle = 'ENVIRONMENT'
        }
        return {
            executionPoint: props.executionPoint,
            sizeInterceptors: props.interceptors.filter(i => i.lifeCycle === lifeCycle).length
        }
    }
}

let collect = (connect, monitor) => {
    return {
        connectDropTarget: connect.dropTarget(),
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop
    }
}

const labelStyle = {
    backgroundColor: 'grey',
    borderRadius: '10px',
    color: '#fff',
    fontSize: '12px',
    fontWeight: 'normal',
    height: '20px',
    lineHeight: '20px',
    position: 'absolute',
    right: 'initial',
    textAlign: 'center',
    textTransform: 'uppercase',
    top: '-10px',
    whiteSpace: 'nowrap',
    width: 100,
    zIndex: '10'
};

class DropClientInterceptors extends Component {

    moveInterceptors = (dragIndex, newIndex, lifeCycleDrag, referenceDrag) => {
        const { interceptors } = this.props
        if (interceptors){
            let newOrderInterceptors = changeOrder(dragIndex, newIndex, interceptors.filter(i => i.lifeCycle === lifeCycleDrag && i.referenceId === referenceDrag))

            newOrderInterceptors.forEach(interceptor => {
                this.props.handleForm(interceptor)
            })
        }
    }

    render() {
        const { canDrop, isOver, connectDropTarget, description } = this.props;
        const isActive = canDrop && isOver;

        let backgroundColor = '#ffffff';

        if (isActive) {
            backgroundColor = '#F7f7bd';
        } else if (canDrop) {
            backgroundColor = '#f7f7f7';
        }

        const style = {
            backgroundColor: backgroundColor,
            padding: 10
        };
        return connectDropTarget(
            <div>
                <Row id="drop-client-interceptors" className="draggable-pane" style={style}>
                    <Row type="flex" justify="center">
                        <sup className="ant-scroll-number" style={labelStyle}>{description}</sup>
                    </Row>
                    {!this.props.interceptors && <Loading />}
                    {this.props.interceptors && this.props.interceptors.map((interceptor, index) => {

                        return <DnDInterceptor
                            key={index}
                            type={interceptor.type}
                            icon='code-o'
                            interceptor={interceptor}
                            order={interceptor.order ? interceptor.order : this.props.interceptors.length}
                            moveInterceptors={this.moveInterceptors}
                            handleForm={this.props.handleForm}
                            handleDelete={this.props.handleDelete}
                        />
                    })}
                </Row>
            </div>
        )
    }
}

DropClientInterceptors.propTypes = {
    connectDropTarget: PropTypes.func.isRequired,
    isOver: PropTypes.bool.isRequired,
    canDrop: PropTypes.func.isRequired
}

export default DropTarget(ItemTypes.INTERCEPTORS, ClientInterceptorsSpec, collect)(DropClientInterceptors)