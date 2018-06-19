import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Link} from 'react-router-dom'

import {Table, Tooltip, Button, Row, Pagination} from 'antd';

const {Column} = Table;

class ListTraces extends Component {

    render() {
        const {dataSource, loading} = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} loading={loading}
                       pagination={false}>
                    <Column title="ID" dataIndex="id" id="id"/>
                    <Column title="URL" dataIndex="url" id="url"/>
                    <Column title="Method" dataIndex="method" id="method"/>
                    <Column title="Status" dataIndex="status" id="status"/>
                    <Column title="Duration" dataIndex="duration" id="duration"/>
                    <Column
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title="Edit">
                                    <Link to={"/traces/" + record.id}><Button type="primary" icon="search"/></Link>
                                </Tooltip>
                            </span>
                        )}
                    />
                </Table>
                <Row type="flex" justify="center" className="h-row">
                    <Pagination total={dataSource.totalElements} onChange={this.props.handlePagination}/>
                </Row>
            </div>
        )
    }
}

ListTraces.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListTraces.defaultProps = {
    dataSource: []
}

export default ListTraces