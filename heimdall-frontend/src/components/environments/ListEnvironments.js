import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { List, Avatar, Tag, Tooltip, Button, Modal, Row, Col } from 'antd'

import i18n from "../../i18n/i18n"

const confirm = Modal.confirm;

class ListEnvironments extends Component {

    showDeleteConfirm = (environmentId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
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
                            <Tooltip title={i18n.t('edit')}>
                                <Link to={"/environments/"+env.id}><Button type="primary" icon="edit" /></Link>
                            </Tooltip>,
                            <Tooltip title={i18n.t('delete')}>
                                <Button type="danger" icon="delete" onClick={this.showDeleteConfirm(env.id)} />
                            </Tooltip>]}>
                            <List.Item.Meta
                                avatar={<Avatar icon="codepen" />}
                                title={env.name}
                                description={
                                    <Row>
                                        <Col sm={24}>
                                            <b>{i18n.t('inbound_url')}:</b> {env.inboundURL}
                                        </Col>
                                        <Col sm={24}>
                                            <b>{i18n.t('outbound_url')}:</b> {env.outboundURL}
                                        </Col>
                                    </Row>
                                }
                            />
                            <span style={{textTransform: 'uppercase'}}>
                                {env.status === 'ACTIVE' && <Tag color="green">{i18n.t('active')}</Tag>}
                                {env.status === 'INACTIVE' && <Tag color="red">{i18n.t('inactive')}</Tag>}
                            </span>
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