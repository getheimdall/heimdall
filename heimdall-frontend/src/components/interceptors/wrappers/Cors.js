import React from 'react'
import { Button } from 'antd'

import i18n from "../../../i18n/i18n"
import PairField from '../../ui/PairField'
import {privileges} from '../../../constants/privileges-types'

class Cors extends React.Component {

    state = {
        cors: {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Credentials': true,
            'Access-Control-Allow-Methods': 'POST, GET, PUT, PATCH, DELETE, OPTIONS',
            'Access-Control-Allow-Headers': 'origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential',
            'Access-Control-Max-Age': 3600,
        },
        count: 1
    }

    componentDidMount() {
        const { content } = this.props

        if (content) {
            this.setState({ ...this.state, cors: content })
        }
    }

    addNewParam = () => {
        let { count, cors } = this.state

        cors[`paramName${count}`] = `paramValue${count}`
        this.setState({ ...this.state, cors, count: count + 1 })
    }

    removePairField = key => {
        let { cors } = this.state
        delete cors[key]
        this.setState({ ...this.state, cors })
    }

    render() {
        const { cors } = this.state

        const keys = Object.keys(cors)

        return (
            <div className="cors-interceptor">
                {
                    keys.map(key => {
                        return <PairField form={this.props.form} key={key} nameKey={key} value={cors[key]} remove={() => this.removePairField(key)} privileges={[privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]}/>
                    })
                }
                <div align="center">
                    <Button style={{ marginTop: 15 }} type="button" onClick={this.addNewParam}>{i18n.t('add_new_param')}</Button>
                </div>
            </div>
        )

    }
}

export default Cors
