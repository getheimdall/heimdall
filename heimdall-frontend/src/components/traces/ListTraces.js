import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Link} from 'react-router-dom'

import {Table, Tooltip, Button, Row, Pagination, Tag} from 'antd';
import ColorUtils from "../../utils/ColorUtils";

const {Column} = Table;

class ListTraces extends Component {

    render() {
        const {dataSource, loading} = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id.toString()} loading={loading}
                       pagination={false}>
                    <Column title="ID" dataIndex="id" id="id"/>
                    <Column title="URL" dataIndex="trace.url" id="url"/>
                    <Column title="Method" dataIndex="trace.method" id="method"/>
                    <Column title="Status" id="trace.resultStatus" key="status" render={(record) => (
                        <span>
                            <Tag color={ColorUtils.getColorStatus(record.trace.resultStatus)}>{record.trace.resultStatus}</Tag>
                        </span>
                    )} />
                    <Column title="Duration" dataIndex="trace.durationMillis" id="duration"/>
                    <Column
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title="View">
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
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListTraces.defaultProps = {
    dataSource: {}

}

export default ListTraces