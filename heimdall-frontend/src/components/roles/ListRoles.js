import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import { Modal, Row, Table, Divider, Tooltip, Button, Pagination } from 'antd';
import ComponentAuthority from "../ComponentAuthority";
import {privileges} from "../../constants/privileges-types";

const confirm = Modal.confirm;
const { Column } = Table;

class ListRoles extends Component {

    showDeleteConfirm = (roleId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(roleId)
            }
        });
    }

    render() {
        const { dataSource, loading } = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} loading={loading} pagination={false}>
                    <Column title="ID" dataIndex="id" id="id" />
                    <Column title="Name" dataIndex="name" id="name" />
                    <Column
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title="Edit">
                                    <Link to={"/roles/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_ROLE]}>
                                    <Divider type="vertical" />
                                    <Tooltip title="Delete">
                                        <Button type="danger" icon="delete" onClick={this.showDeleteConfirm(record.id)} />
                                    </Tooltip>
                                </ComponentAuthority>
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

ListRoles.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListRoles.defaultProps = {
    dataSource:{}
}

export default ListRoles