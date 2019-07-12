import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { Modal, Row, Table, Divider, Tag, Tooltip, Button, Pagination } from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {privileges} from "../../constants/privileges-types"

const confirm = Modal.confirm
const { Column } = Table

class ListPlans extends Component {

    showDeleteConfirm = (planId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(planId)
            }
        });
    }

    render() {
        const { dataSource } = this.props
        const { loading } = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x:694}} loading={loading} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" />
                    <Column title={i18n.t('name')} dataIndex="name" id="name" />
                    <Column title={i18n.t('description')} dataIndex="description" id="description" />
                    <Column title={i18n.t('api')} dataIndex="api.name" id="apiName" />
                    <Column title={i18n.t('status')} id="status" key="status" render={(record) => (
                        <span style={{textTransform: 'uppercase'}}>
                            {record.status === 'ACTIVE' && <Tag color="green">{i18n.t('active')}</Tag>}
                            {record.status === 'INACTIVE' && <Tag color="red">{i18n.t('inactive')}</Tag>}
                        </span>
                    )} />
                    <Column
                        align="right"
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('edit')}>
                                    <Link to={"/plans/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_PLAN]}>
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

ListPlans.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListPlans.defaultProps = {
    dataSource:
    [
        {
          "api": {
            "basePath": "/base/path",
            "description": "description",
            "id": 1,
            "name": "default name",
            "status": "ACTIVE"
          },
          "creationDate": "2018-03-27T17:55:45.329Z",
          "description": "Some description",
          "id": 1,
          "name": "Some plan",
          "status": "ACTIVE"
        }
      ]
}

export default ListPlans