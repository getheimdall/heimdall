import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { Modal, Table, Divider, Tag, Tooltip, Button, Row, Pagination } from 'antd'

import i18n from "../../i18n/i18n"

const confirm = Modal.confirm;
const { Column } = Table;

class ListUsers extends Component {

    showDeleteConfirm = (userId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(userId)
            }
        });
    }

    render() {
        const { dataSource, loading } = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x: 626}} loading={loading} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" />
                    <Column title={i18n.t('username')} dataIndex="userName" id="name" />
                    <Column title={i18n.t('email')} dataIndex="email" id="email" />
                    <Column title={i18n.t('status')} id="status" key="status" render={(record) => (
                        <span style={{textTransform: 'uppercase'}}>
                            {record.status === 'ACTIVE' && <Tag color="green">{i18n.t('active')}</Tag>}
                            {record.status === 'INACTIVE' && <Tag color="red">{i18n.t('inactive')}</Tag>}
                        </span>
                    )} />
                    <Column
                        title={i18n.t('action')}
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('edit')}>
                                    <Link to={"/users/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <Divider type="vertical" />
                                <Tooltip title={i18n.t('delete')}>
                                    <Button type="danger" icon="delete" onClick={this.showDeleteConfirm(record.id)} />
                                </Tooltip>
                            </span>
                        )}
                    />
                </Table>
                <Row type="flex" justify="center" className="h-row">
                    <Pagination total={dataSource.totalElements} onChange={this.props.handlePagination} />
                </Row>
            </div>
        )
    }
}

ListUsers.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListUsers.defaultProps = {
    dataSource:[]
}

export default ListUsers