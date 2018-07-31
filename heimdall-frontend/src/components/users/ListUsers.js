import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import { Modal, Table, Divider, Tag, Tooltip, Button, Row, Pagination } from 'antd';

const confirm = Modal.confirm;
const { Column } = Table;

class ListUsers extends Component {

    showDeleteConfirm = (userId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
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
                    <Column title="ID" dataIndex="id" id="id" />
                    <Column title="Username" dataIndex="userName" id="name" />
                    <Column title="Email" dataIndex="email" id="email" />
                    <Column title="Status" id="status" key="status" render={(record) => (
                        <span>
                            {record.status === 'ACTIVE' && <Tag color="green">{record.status}</Tag>}
                            {record.status === 'INACTIVE' && <Tag color="red">{record.status}</Tag>}
                        </span>
                    )} />
                    <Column
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title="Edit">
                                    <Link to={"/users/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <Divider type="vertical" />
                                <Tooltip title="Delete">
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