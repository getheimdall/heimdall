import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import { Modal, Row, Table, Divider, Tag, Tooltip, Button, Pagination } from 'antd';

const confirm = Modal.confirm;
const { Column } = Table;

class ListDevelopers extends Component {

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
        const { dataSource } = this.props
        const { loading } = this.props
        return (    
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x: 672}} loading={loading} pagination={false}>
                    <Column title="ID" dataIndex="id" id="id" />
                    <Column title="Name" dataIndex="name" id="name" />
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
                                    <Link to={"/developers/" + record.id}><Button type="primary" icon="edit" /></Link>
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

ListDevelopers.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListDevelopers.defaultProps = {
    dataSource:
        [{
            id: '1',
            name: 'John John',
            email: 'email1@modal.com.br',
            status: 'ACTIVE',
        }, {
            id: '2',
            name: 'Jim Parson',
            email: 'email2@modal.com.br',
            status: 'ACTIVE',
        }, {
            id: '3',
            name: 'Joe Tolanski',
            email: 'email2@modal.com.br',
            status: 'INACTIVE',
        },
        {
            id: '4',
            name: 'Jo Orski',
            email: 'email3@modal.com.br',
            status: 'ACTIVE',
        }]
}

export default ListDevelopers