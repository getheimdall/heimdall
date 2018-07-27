import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import { List, Avatar, Tag, Tooltip, Button, Modal, Row, Col } from 'antd';

const confirm = Modal.confirm;

class ListEnvironments extends Component {

    showDeleteConfirm = (environmentId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(environmentId)
            }
        });
    }

    render() {
        return (
            <div>
                <List
                    itemLayout="horizontal"
                    className="list-environments"
                    bordered
                    dataSource={this.props.environments}
                    renderItem={env => (
                        <List.Item actions={[
                            <Tooltip title="Edit">
                                <Link to={"/environments/"+env.id}><Button type="primary" icon="edit" /></Link>
                            </Tooltip>,
                            <Tooltip title="Delete">
                                <Button type="danger" icon="delete" onClick={this.showDeleteConfirm(env.id)} />
                            </Tooltip>]}>
                            <List.Item.Meta
                                avatar={<Avatar icon="codepen" />}
                                title={env.name}
                                description={
                                    <Row>
                                        <Col sm={24}>
                                            <b>Inbound URL:</b> {env.inboundURL}
                                        </Col>
                                        <Col sm={24}>
                                            <b>Outbound URL:</b> {env.outboundURL}
                                        </Col>
                                    </Row>
                                }
                            />
                            {env.status === 'ACTIVE' && <Tag color="green">{env.status}</Tag>}
                            {env.status === 'INACTIVE' && <Tag color="red">{env.status}</Tag>}
                        </List.Item>
                    )}
                />
            </div>
        )
    }
}

ListEnvironments.propTypes = {
    environments: PropTypes.array.isRequired
}

export default ListEnvironments