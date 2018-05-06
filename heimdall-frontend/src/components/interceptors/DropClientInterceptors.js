import React, { Component } from 'react'
import { DropTarget } from 'react-dnd'
import PropTypes from 'prop-types'
import ItemTypes from '../../constants/items-types'
import { Row } from 'antd'

import DnDInterceptor from './DnDInterceptor'
import Loading from '../ui/Loading';

const ClientInterceptorsSpec = {
    drop(props) {
        return {
            executionPoint: props.executionPoint
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

class DropClientInterceptors extends Component {

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
            padding: 20
        };
        return connectDropTarget(
            <div>
                <Row id="drop-client-interceptors" className="draggable-pane" style={style}>
                    <Row type="flex" justify="center">
                        <sup className="ant-scroll-number ant-badge-count" style={{backgroundColor: 'grey', width: 100}}>{description}</sup>
                    </Row>
                    {!this.props.interceptors && <Loading />}
                    {this.props.interceptors && this.props.interceptors.map((interceptor, index) => {
                        let color
                        if (interceptor.lifeCycle === 'PLAN') {
                            color = '#c3cc93'
                        } else if (interceptor.lifeCycle === 'RESOURCE') {
                            color = '#8edce0'
                        } else if (interceptor.lifeCycle === 'OPERATION') {
                            color = '#607d8b'
                        }
                        return <DnDInterceptor
                            key={index}
                            type={interceptor.type}
                            icon='code-o'
                            color={color}
                            interceptor={interceptor}
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