import React, {Component} from 'react'
import {connect} from 'react-redux'

import {middlewareService} from "../services/MiddlewareService";

import {Upload, message, Icon} from 'antd'

const Dragger = Upload.Dragger;

class Middlewares extends Component {

    state = {
        environmentId: 0,
        planId: 0,
        resourceId: 0,
        operationId: 0,
        planSelected: false,
        resourceSelected: false,
        operationSelected: false,
        candidatesToSave: [],
        candidatesToUpdate: [],
        candidatesToDelete: [],
        showProgress: false,
        progress: 0,
        apiId: this.props.api.id
    }

    handleChangeUpload = (info) => {
        const status = info.file.status
        if (status === 'done') {
            message.success(`${info.file.name} file uploaded successfully.`)
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`)
        }
    }

    customRequest = (info) => {
        const {apiId} = this.state
        const data = new FormData()
        console.log(info)
        data.append("file", info.file)
        data.append("version", "2.2.13")
        data.append("status", "ACTIVE")

        middlewareService.save(data, apiId).then((data) => {
            console.log(data)
            info.onSuccess(() => {
                message.success(`${info.file.name} file uploaded successfully.`)
            })
        }).catch((error) => {
            console.log(error)
            info.onError(() => {
                message.error(`${info.file.name} file upload failed.`)
            })
        })
    }

    render() {

        const propsFileUpload = {
            name: 'file',
            multiple: false,
            onChange: this.handleChangeUpload,
            beforeUpload(file) {
                const splitName = file.name.split('.')
                const type = splitName[splitName.length-1]
                const isJar = type === "jar"
                if (!isJar) {
                    message.error('You can only upload JPG file!')
                }

                const isLtOrE25M = file.size / 1024 / 1024 <= 25;
                if (!isLtOrE25M) {
                    message.error('Jar must smaller or equal than 25MB!')
                }

                return isJar && isLtOrE25M
            },
            accept: '.jar',
            customRequest: this.customRequest
        };

        return (
            <div>
                <Dragger {...propsFileUpload}>
                    <p className="ant-upload-drag-icon">
                        <Icon type="inbox"/>
                    </p>
                    <p className="ant-upload-text">Click or drag middleware to this area to upload</p>
                    <p className="ant-upload-hint">Support for a single upload. Strictly prohibit from uploading
                        company data or other band files</p>
                </Dragger>
            </div>
        )
    }
}


const mapStateToProps = state => {
    return {
        interceptors: state.interceptors.interceptors,
        loading: state.interceptors.loading,
        interceptorTypes: state.interceptors.interceptorTypes,
        plans: state.plans.plans,
        resources: state.resources.resources,
        operations: state.operations.operations,
        notification: state.interceptors.notification,
        queueCount: state.queue.count
    }
}

export default connect(mapStateToProps) (Middlewares)