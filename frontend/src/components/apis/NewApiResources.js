import React, { Component } from 'react'
import update from 'react-addons-update'
import { Button, Tooltip, Row, Col, Form, Input, Select, message, Modal } from 'antd'
// import ListResource from './ListResources'
import ReactCSSTransitionGroup from 'react-addons-css-transition-group'

const FormItem = Form.Item
const Option = Select.Option
const confirm = Modal.confirm

class NewApiResource extends Component {
    constructor(props) {
        super(props)
        this.state = {
            resources: [],
            newResource: {
                id: 0,
                apiId: 0,
                name: '',
                description: '',
                database: null,
                operations: []
            },
            fromAdd: false
        }
        this.handleAddResource = this.handleAddResource.bind(this)
        this.onSubmitResource = this.onSubmitResource.bind(this)
        this.deleteResource = this.deleteResource.bind(this)
    }

    componentDidMount() {
        this.props.onRef(this)
        // set api id from associate resources
        // ! this is not best way
        if (this.props.api) {
            const id = this.props.api.id
            const newState = update(this.state, {newResource: {apiId: {$set: id}}})
            this.setState(newState)
        } 
    }

    componentWillMount() {
        this.props.onRef(undefined)
    }

    // delete resource
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

    // toggle add resource menu
    handleAddResource() {
        const { fromAdd } = this.state
        if (fromAdd)
            this.setState({ fromAdd: false })
        else
            this.setState({ fromAdd: true })
    }

    onSubmitResource(e) {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
          if (!err) {
            let newState = update(this.state, {newResource: {id: {$set: Date.now()}}})
            newState = update(newState, { resources: {$push: [newState.newResource]} })

            // dispatch to store
            this.setState(newState)
            message.success('Resource saved!')
            this.handleAddResource()
            const newApi = update(this.props.api.resources, {$set: newState})
            this.props.api.resources.push(newApi.newResource)
          }
        })
    }

    completeResource() {
        this.props.next(this.props.api)
    }

    render() { 
        const { resources, fromAdd } = this.state
        const { getFieldDecorator } = this.props.form

        return (
            <div>
                <Form id="new_api_resource">
                    <div style={{textAlign: 'right'}} >
                        {
                            fromAdd
                            ? (
                                <Button.Group size="large">
                                    <Tooltip title="Save">
                                        <Button type="primary" icon="save" size="large" 
                                            onClick={this.onSubmitResource} id="saveApiResource"
                                        />
                                    </Tooltip>
                                    <Tooltip title="Cancel">
                                        <Button id="cancelApiResource" type="primary" icon="close" size="large" onClick={this.handleAddResource} />
                                    </Tooltip>
                                </Button.Group>
                            )
                            : (
                                <Tooltip title="Add resource">
                                    <Button id="addResource" type="primary" icon="plus" shape="circle" size="large" onClick={this.handleAddResource} />
                                </Tooltip>
                            )
                        }
                        <Tooltip title="Swagger Editor">
                            <Button type="primary" icon="code-o" shape="circle" size="large" ghost style={{marginLeft:8}} />
                        </Tooltip>
                        <Tooltip title="Import WSDL">
                            <Button type="primary" icon="download" shape="circle" size="large" ghost style={{marginLeft:8}} />
                        </Tooltip>
                        <Tooltip title="Import from database">
                            <Button type="primary" icon="database" shape="circle" size="large" ghost style={{marginLeft:8}} />
                        </Tooltip>
                    </div>
                    {
                        fromAdd
                        ? (
                            <ReactCSSTransitionGroup
                                transitionName="appear"
                                transitionAppear={true}
                                transitionAppearTimeout={500}
                                transitionEnter={false}
                                transitionLeave={false}
                            >
                                <Row  className="h-row no-mobile-padding" type="flex" justify="space-between" align="bottom" gutter={20}>
                                    <Col sm={24} md={9}>
                                        <FormItem
                                            label="Name"
                                        >
                                            {getFieldDecorator('resourceName', {
                                                rules: [{ required: true, message: 'Please, input resource name!' }]
                                            })(
                                                <Input
                                                    id="add_resource_name"
                                                    name="add_resource_name"
                                                    onChange={(e) => { 
                                                        this.setState({ 
                                                            newResource: update(
                                                                this.state.newResource, 
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
                                                rules: [{ required: true, message: 'Please, input resource description!' }]
                                            })(
                                                <Input
                                                    id="add_resource_description"
                                                    name="add_resource_description"
                                                    onChange={(e) => { 
                                                        this.setState({ 
                                                            newResource: update(
                                                                this.state.newResource, 
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
                                                onChange={(e) => { 
                                                    this.setState({ 
                                                        newResource: update(
                                                            this.state.newResource, 
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
                            </ReactCSSTransitionGroup>
                        ) : null
                    }
                </Form>
                {resources && resources.length > 0
                    ? (
                        <Row type="flex" justify="space-between" align="bottom" gutter={20}>
                            <Col sm={24}>
                                {/* <ListResource 
                                    resources={resources} 
                                    deleteResource={this.deleteResource}
                                    history={this.props.history}
                                /> */}
                            </Col>
                        </Row>
                    ) : null
                }
            </div>
        )
    }
}

const WrappedNewApiResource = Form.create()(NewApiResource)
 
export default WrappedNewApiResource