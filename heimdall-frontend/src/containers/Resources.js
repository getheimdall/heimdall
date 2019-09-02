import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {connect} from 'react-redux'
import {bindActionCreators} from 'redux'
import {Button, Col, Modal, notification, Row, Tooltip} from 'antd'

import i18n from "../i18n/i18n"
import Operations from './Operations'
import Loading from '../components/ui/Loading'
import HeimdallCollapse from '../components/collapse'
import {PrivilegeUtils} from "../utils/PrivilegeUtils"
import {privileges} from '../constants/privileges-types'
import ResourceForm from '../components/resources/ResourceForm'
import ComponentAuthority from "../components/policy/ComponentAuthority"
import {clearResources, getAllResourcesByApi, remove, resetResource, toggleModal} from '../actions/resources'

const HeimdallPanel = HeimdallCollapse.Panel;
const ButtonGroup = Button.Group;

class Resources extends Component {

    constructor(props) {
        super(props)
        this.state = {
            loadedKeys: [],
            resourceSelected: 0
        }

        this.callback = this.callback.bind(this)
        this.addResourceModal = this.addResourceModal.bind(this)
        this.updateResourceModal = this.updateResourceModal.bind(this)
        this.handleSave = this.handleSave.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
    }

    componentDidMount() {
        let idApi = this.props.api.id
        if (idApi) {
            this.props.getResourcesByApi(idApi)
        }
        this.props.toggleModal(false)
    }

    componentWillUnmount() {
        this.props.clearResources()
        // this.setState({...this.state, steps: []})
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({message, description})
        }
    }

    callback(keys) {
        keys.forEach(key => {
            //verificar se essa key está presente nos items carregados
            if (!this.state.loadedKeys.includes(key)) {
                //se nao estiver, adicionar as keys que foram abertas e buscar os operations desse recurso.
                this.setState({...this.state, loadedKeys: [...this.state.loadedKeys, key]})
            }
        })
    }

    addResourceModal() {
        this.props.toggleModal(true)
    }

    updateResourceModal = (resourceId) => (e) => {
        this.setState({...this.state, resourceSelected: resourceId});
        this.props.toggleModal(true)
    }

    handleSave(e) {
        this.addResource.onSubmitResource()
        this.props.clearResources()
        this.props.resetResource()
        this.setState({...this.state, resourceSelected: 0});
    }

    handleCancel(e) {
        this.props.toggleModal(false)
        this.setState({...this.state, resourceSelected: 0});
    }

    remove = (idApi, resourceId) => (e) => {
        this.props.remove(idApi, resourceId)
        this.props.clearResources()
    }

    render() {
        const {api} = this.props
        const {resources} = this.props
        const {loading} = this.props
        if (!resources) return <Loading/>

        const modalResource =
            <Modal title={i18n.t('add_resource')}
                   footer={[
                       <Button id="cancelAddResource" key="back" onClick={this.handleCancel}>{i18n.t('cancel')}</Button>,
                       <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_UPDATE_RESOURCE]}>
                           <Button id="saveResource" key="submit" type="primary" loading={loading} onClick={this.handleSave}>
                               {i18n.t('save')}
                           </Button>
                       </ComponentAuthority>
                   ]}
                   visible={this.props.visibleModal}
                   onCancel={this.handleCancel}
                   destroyOnClose >
                <ResourceForm onRef={ref => (this.addResource = ref)} resourceId={this.state.resourceSelected} idApi={api.id} />
            </Modal>

        if (resources && resources.length === 0) {
            return (
                <Row type="flex" justify="center" align="bottom">
                    { PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_RESOURCE]) &&
                        <Col style={{marginTop: 20}}>
                            {i18n.t('you_do_not_have_resources_in_this')} <b>{i18n.t('api')}</b>! <Button id="addResourceWhenListIsEmpty" type="dashed" className="add-tour" onClick={this.addResourceModal}>{i18n.t('add_resource')}</Button>
                        </Col>
                    }
                    { !PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_RESOURCE]) &&
                        <Col style={{marginTop: 20}}>
                            {i18n.t('you_do_not_have_resources_in_this')} <b>{i18n.t('api')}</b>!
                        </Col>
                    }
                    {modalResource}
                </Row>
            )
        }

        return (
            <Row>
                <HeimdallCollapse onChange={this.callback}>
                    {resources.map((resource, index) => {
                        return (
                            <HeimdallPanel className={index === 0 ? "header-tour" : ''} header={resource.name}
                                           key={resource.id} extra={
                                <Row type="flex" justify="center">
                                    <ButtonGroup>
                                        <Tooltip title={i18n.t('edit')}>
                                            <Button type="primary" icon="edit" onClick={this.updateResourceModal(resource.id)} />
                                        </Tooltip>
                                        <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_RESOURCE]}>
                                            <Tooltip title={i18n.t('delete')}>
                                                <Button type="danger" icon="delete"
                                                        onClick={this.remove(api.id, resource.id)}/>
                                            </Tooltip>
                                        </ComponentAuthority>
                                    </ButtonGroup>
                                </Row>
                            } extraWidth={10}>
                                <Operations idResource={resource.id} idApi={api.id} apiBasepath={api.basePath} />
                            </HeimdallPanel>
                        )
                    })}
                </HeimdallCollapse>
                <br/>
                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_RESOURCE]}>
                    <Row type="flex" justify="end">
                        <Tooltip title={i18n.t('add_resource')}>
                            <Button id="addResource" className="card-button add-tour" type="primary" icon="plus" onClick={this.addResourceModal} size="large" shape="circle" />
                        </Tooltip>
                    </Row>
                </ComponentAuthority>
                {modalResource}
            </Row>
        )
    }
}

Resources.propTypes = {
    api: PropTypes.object.isRequired
}

const mapStateToProps = state => {
    return {
        resources: state.resources.resources,
        operations: state.operations.list,
        visibleModal: state.resources.visibleModal,
        loading: state.resources.loading,
        notification: state.resources.notification
    }
}

const mapDispatchToProps = dispatch => {
    return {
        getResourcesByApi: bindActionCreators(getAllResourcesByApi, dispatch),
        clearResources: bindActionCreators(clearResources, dispatch),
        resetResource: bindActionCreators(resetResource, dispatch),
        toggleModal: bindActionCreators(toggleModal, dispatch),
        remove: bindActionCreators(remove, dispatch)
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Resources)