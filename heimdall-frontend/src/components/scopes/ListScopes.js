import React, { Component } from 'react'
import PropTypes from 'prop-types'
import i18n from "../../i18n/i18n"

import { Modal, Row, Table, Divider, Tooltip, Button, Pagination } from 'antd';
import ComponentAuthority from "../policy/ComponentAuthority";
import {privileges} from "../../constants/privileges-types";

const confirm = Modal.confirm;
const { Column } = Table;

class ListScopes extends Component {

    showDeleteConfirm = (id) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(id)
            }
        });
    }

    render() {
        const { dataSource, loading } = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} loading={loading} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" />
                    <Column title={i18n.t('name')} dataIndex="name" id="name" />
                    <Column title={i18n.t('description')} dataIndex="description" id="description" />
                    <Column
                        id="action"
                        key="action"
                        align="right"
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('edit')}>
                                    <Button type="primary" icon="edit" onClick={this.props.handleEdit(record.id)} />
                                </Tooltip>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_SCOPE]}>
                                    <Divider type="vertical" />
                                    <Tooltip title={i18n.t('delete')}>
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

ListScopes.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListScopes.defaultProps = {
    dataSource:{}
}

export default ListScopes