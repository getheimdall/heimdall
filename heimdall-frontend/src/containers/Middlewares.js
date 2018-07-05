import React, {Component} from 'react'
import {connect} from 'react-redux'
import {Upload, message, Icon, Input, Row, Card, Table, Pagination, Tag} from 'antd'
import {save, initLoading, getMiddlewares} from '../actions/middlewares'

const Dragger = Upload.Dragger
const Column = Table.Column

class Middlewares extends Component {

    state = {
        apiId: this.props.api.id,
        version: "",
        page: 0,
        pageSize: 10
    }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getMiddlewares({offset: 0, limit: 10}, this.state.apiId))
    }

    handleChangeUpload = (info) => {
        const status = info.file.status
        if (status === 'done') {
            this.setState({...this.state, version: ""})
            message.success(`${info.file.name} file uploaded successfully.`)

        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`)
            message.error(info.file.error.response.data.message)
        }
    }

    handlePagination = (page, pageSize) => {
        this.setState({...this.state, page: page - 1, pageSize: pageSize})
        this.props.dispatch(initLoading())
        this.props.dispatch(getMiddlewares({offset: page - 1, limit: 10}, this.state.apiId))
    }

    handleBeforeUpload = (file) => {
        const splitName = file.name.split('.')
        const type = splitName[splitName.length - 1]
        const isJar = type === "jar"
        if (!isJar) {
            message.error('You can only upload JPG file!')
        }

        const isLtOrE25M = file.size / 1024 / 1024 <= 25;
        if (!isLtOrE25M) {
            message.error('Jar must smaller or equal than 25MB!')
        }

        return isJar && isLtOrE25M
    }

    sendFileUpload = (info) => {
        const {apiId, version} = this.state
        const data = new FormData()
        data.append("file", info.file)
        data.append("version", version)
        data.append("status", "ACTIVE")

        this.props.dispatch(save(data, apiId, info))
    }

    handleOnChangeVersion = (event) => {
        this.setState({...this.state, version: event.target.value})
    }

    verifyVersionIsEmpty = () => {
        return this.state.version.length === 0
    }

    render() {

        const {loading, middlewares} = this.props

        const propsFileUpload = {
            name: 'file',
            multiple: false,
            onChange: this.handleChangeUpload,
            beforeUpload: this.handleBeforeUpload,
            accept: '.jar',
            customRequest: this.sendFileUpload
        };



        console.log(middlewares)

        return (
            <div>
                <Card
                    title="Upload middleware"
                    style={{marginBottom: 20}}
                    className="inside-shadow"
                >
                    <Row>
                        <Input placeholder="version" onChange={(event) => this.handleOnChangeVersion(event)}
                               addonBefore="Version" value={this.state.version}/>
                    </Row>
                    <br/>
                    <Row>
                        <Dragger {...propsFileUpload} disabled={this.verifyVersionIsEmpty()}>
                            <p className="ant-upload-drag-icon">
                                <Icon type="inbox"/>
                            </p>
                            <p className="ant-upload-text">Click or drag middleware to this area to upload</p>
                            <p className="ant-upload-hint">Support for a single upload. Strictly prohibit from uploading
                                company data or other band files</p>
                        </Dragger>
                    </Row>
                </Card>
                <Card
                    title="Versions middlewares"
                    style={{marginBottom: 20}}
                    className="inside-shadow"
                >
                    {
                        middlewares &&
                        <div>
                            <Table dataSource={middlewares.content} loading={loading} rowKey={record => record.id}
                                   pagination={false} >
                                <Column title="ID" dataIndex="id" id="id"/>
                                <Column title="Path" dataIndex="path" id="path"/>
                                <Column title="Version" dataIndex="version" id="version"/>
                                <Column title="Status" id="status" key="status" render={(record) => (
                                    <span>
                                        {record.status === 'ACTIVE' && <Tag color="green">{record.status}</Tag>}
                                        {record.status === 'INACTIVE' && <Tag color="red">{record.status}</Tag>}
                                    </span>
                                )}/>
                                <Column title="Created on" dataIndex="creationDate"/>
                            </Table>
                            <Row type="flex" justify="center" className="h-row">
                                <Pagination total={middlewares.totalElements} onChange={this.handlePagination}/>
                            </Row>
                        </div>
                    }

                </Card>
            </div>
        )
    }
}


const mapStateToProps = state => {
    return {
        middlewares: state.middlewares.middlewares,
        loading: state.middlewares.loading,
        notification: state.middlewares.notification
    }
}

export default connect(mapStateToProps)(Middlewares)