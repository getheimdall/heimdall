import React, { Component } from 'react'
import { Card, Button, Icon, Form, Input, Select, Row, Col, Divider, message, Modal, notification } from 'antd'
import update from 'react-addons-update'
import PageHeader from '../ui/PageHeader'
import FloatMenu from '../ui/FloatMenu'
import ListResourceOperations from './ListResourceOperations'
import ResourceOperationForm from './ResourceOperationForm'
import EditOperation from './EditOperation'

const FormItem = Form.Item
const Option = Select.Option
const confirm = Modal.confirm

class SingleResource extends Component {
    constructor(props) {
        super(props)
        this.state={
            resource: {},
            newOperation: {
                id: 0,
                method: 'GET',
                path: '',
                description: '',
                async: false
            },
            operationForm: false,
            visibleEditOp: false
        }
        this.goBack = this.goBack.bind(this)
        this.toggleOperationForm = this.toggleOperationForm.bind(this)
        this.handleOperationMethod = this.handleOperationMethod.bind(this)
        this.handleOperationPath = this.handleOperationPath.bind(this)
        this.handleOperationDescription = this.handleOperationDescription.bind(this)
        this.handleOperationAsync = this.handleOperationAsync.bind(this)
        this.editOperation = this.editOperation.bind(this)
        this.saveOperation = this.saveOperation.bind(this)
        this.deleteOperation = this.deleteOperation.bind(this)
        this.hideModal = this.hideModal.bind(this)
        this.validateForm = this.validateForm.bind(this)
        this.updateAsync = this.updateAsync.bind(this)
        this.updateResource = this.updateResource.bind(this)
    }

    componentDidMount() {
        const resource = this.props.location.state.resource
        if (resource) {
            this.setState({ resource: resource })
        }
    }

    goBack() {
        this.props.history.goBack()
    }

    toggleOperationForm() {
        const b = !this.state.operationForm
        const newState = {
            id: 0,
            method: 'GET',
            path: '',
            description: '',
            async: false
        }

        this.setState({ operationForm: b, newOperation: newState })
    }

    handleOperationMethod(e) {
        const newState = update(this.state, { newOperation: {method: {$set: e.toUpperCase()}} })
        this.setState(newState)
    }

    handleOperationPath(e) {
        const newState = update(this.state, { newOperation: {path: {$set: e.target.value}} })
        this.setState(newState)
    }

    handleOperationDescription(e) {
        const newState = update(this.state, { newOperation: {description: {$set: e.target.value}} })
        this.setState(newState)
    }

    handleOperationAsync(e) {
        const newState = update(this.state, { newOperation: {async: {$set: e.target.checked}} })
        this.setState(newState)
    }

    editOperation(e, row) {
        const newOperation = row.original
        this.setState({ newOperation: newOperation, visibleEditOp: true })
    }

    deleteResource(e, resourceId) {
        const resourceIndex = this.state.resources.findIndex(r => r.id===resourceId)
        const newState = update(this.state, {resources: {$splice: [[resourceIndex, 1]]}})
        let _ = this

        confirm({
            title: 'Are you sure delete this resource?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk() {
                _.setState(newState)
                message.success('Resource removed!')
            },
            onCancel() {
                message.error('Action canceled')
            },
        });
    }

    deleteOperation(e, row) {
        const opId = row.original.id
        const index = this.state.resource.operations.findIndex(o => o.id===opId)
        const newState = update(this.state.resource, {operations: {$splice: [[index, 1]]}}) 
        const _ = this

        confirm({
            title: 'Are you sure delete this operation?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk() {
                _.setState({ resource: newState })
                message.success('Operation removed!')
            },
            onCancel() {
                message.error('Action canceled')
            },
        });
    }

    // update operation async in table
    updateAsync(e, operation) {
        const opId = operation.original.id
        const b = operation.original.async
        const index = this.state.resource.operations.findIndex(o => o.id===opId)
        const newState = update(this.state.resource, {operations: {[index]: {async: {$set: !b}}}})
        this.setState({ resource: newState })
        message.success('Operation async updated!')
    }

    saveOperation() {
        const opId = this.state.newOperation.id
        const index = this.state.resource.operations.findIndex(op => op.id===opId)
        const operations = update(this.state.resource, {operations: {[index]: {$set: this.state.newOperation}}})
        this.setState({ visibleEditOp: false, resource: operations })
        message.success('Operation updated!')
    }

    hideModal = () => {
        const newState = {
            id: 0,
            method: 'GET',
            path: '',
            description: '',
            async: false
        }
        this.setState({
          visibleEditOp: false,
          newOperation: newState
        });
    }

    validateForm() {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const operation = update(this.state.newOperation, {id: {$set: Date.now()}})
                const resource = update(this.state.resource,  {operations: {$push: [operation]}})
              
                this.setState({ newOperation: operation, resource: resource })
                message.success('Operation saved!')
                
                this.toggleOperationForm()
            }
        })
    }

    // update resource and goBack
    updateResource() {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                notification['success']({
                    message: 'Resource saved'
                })
                this.props.history.goBack()
            }
        })
    }

    render() { 
        const { resource, operationForm, newOperation, visibleEditOp } = this.state
        const { getFieldDecorator } = this.props.form

        return (
            <div>
                <PageHeader
                    title="APIs"
                    icon="api"
                />

                <Card
                    title={resource.name}
                    extra={<Button type="primary" onClick={this.goBack}><Icon type="left" /> Go back</Button>}
                >
                    <Form id="edit_single_resource">
                        <Row  className="h-row no-mobile-padding" type="flex" justify="space-between" align="bottom" gutter={20}>
                            <Col sm={24} md={9}>
                                <FormItem label="Name">
                                    {getFieldDecorator('resourceName', {
                                        rules: [{ required: true, message: 'Please, input resource name!' }],
                                        initialValue: resource.name
                                    })(
                                        <Input
                                            id="add_resource_name"
                                            name="add_resource_name"
                                            onChange={(e) => { 
                                                this.setState({ 
                                                    resource: update(
                                                        resource, 
                                                        {name: {$set:e.target.value}}
                                                    )
                                                }) 
                                            }}
                                            required
                                        />
                                    )}
                                </FormItem>
                            </Col>
                    
                            <Col sm={24} md={9}>
                                <FormItem
                                    label="Description"
                                >
                                    {getFieldDecorator('resourceDescription', {
                                        rules: [{ required: true, message: 'Please, input resource description!' }],
                                        initialValue: resource.description
                                    })(
                                        <Input
                                            id="add_resource_description"
                                            name="add_resource_description"
                                            onChange={(e) => { 
                                                this.setState({ 
                                                    resource: update(
                                                        resource, 
                                                        {description: {$set:e.target.value}}
                                                    )
                                                }) 
                                            }}
                                            required
                                        />
                                    )}
                                </FormItem>
                            </Col>
                    
                            <Col sm={24} md={6}>
                                <FormItem label="Add Database"> 
                                    <Select
                                        placeholder="Do not use"
                                        id="add_resource_database"
                                        name="add_resource_database"
                                        value={resource.database}
                                        onChange={(e) => { 
                                            this.setState({ 
                                                resource: update(
                                                    resource, 
                                                    {database: {$set:e}}
                                                )
                                            }) 
                                        }}
                                    >
                                        <Option value="heimdall">heimdall</Option>
                                        <Option value="pear">pear</Option>
                                    </Select>
                                </FormItem>
                            </Col>
                        </Row>
                    </Form>

                    <Divider />

                    <Row className="h-row no-mobile-padding">                      
                        <Col sm={12}>
                            <h4 style={{opacity:0.6}} ><Icon type="sync" /> Operations</h4>
                        </Col>

                        {
                            operationForm
                            ? (
                                <div>
                                    <Col sm={12} style={{textAlign:'right', marginBottom: 30}} >
                                        <Button type="primary" ghost onClick={this.toggleOperationForm} icon="close" shape="circle" />
                                    </Col>
                                    
                                    <Col sm={24} style={{marginBottom: 30}} >
                                        <ResourceOperationForm 
                                            handleOperationMethod={this.handleOperationMethod}
                                            handleOperationPath={this.handleOperationPath}
                                            handleOperationDescription={this.handleOperationDescription}
                                            handleOperationAsync={this.handleOperationAsync}
                                            toggleForm={this.toggleOperationForm}
                                            validateForm={this.validateForm}
                                            form={this.props.form}
                                        />
                                    </Col>
                                </div>
                            ) : (
                                <Col sm={12} style={{textAlign:'right', marginBottom: 30}} >
                                    <Button id="addOperation" type="primary" ghost onClick={this.toggleOperationForm}>
                                        <Icon type="plus" /> Add operation
                                    </Button>
                                </Col>
                            )
                        }

                        <Col sm={24}>
                            <ListResourceOperations 
                                operations={resource.operations}
                                editOperation={this.editOperation}
                                changeAsync={this.updateAsync}
                                deleteOperation={this.deleteOperation}
                            />
                        </Col>
                    </Row>
                </Card>

                <Modal
                    title="Edit operation"
                    visible={visibleEditOp}
                    onOk={this.saveOperation}
                    onCancel={this.hideModal}
                    okText="Save"
                    cancelText="Cancel"
                >
                    <EditOperation
                        operation={newOperation}
                        handleOperationMethod={this.handleOperationMethod}
                        handleOperationPath={this.handleOperationPath}
                        handleOperationDescription={this.handleOperationDescription}
                        handleOperationAsync={this.handleOperationAsync}
                        form={this.props.form}
                    />
                </Modal>

                <FloatMenu
                    saveFunction={this.updateResource}
                    history={this.props.history}
                />
            </div>
        )
    }
}

const WrapComponent = Form.create()(SingleResource)
 
export default WrapComponent