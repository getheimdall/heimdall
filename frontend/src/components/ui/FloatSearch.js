import React from 'react'
import PropTypes from 'prop-types'
import { Input, Button } from 'antd'

const searchStyle = {
    position: 'absolute',
    background: '#F4F7F8',
    top: -6,
    left: '50%',
    width: '30%',
    transform: 'translateX(-50%)',
    display: 'flex',
    alignItems: 'center',
    zIndex: 100,
    textAlign: 'center',
    padding: 15,
    borderRadius: 3,
    minWidth: 340,
    boxShadow: '0 0 20px -2px #F4F7F8'
}

const FloatSearch = props => (
    <div className="heimdall-float-search" style={searchStyle}>
        <Input type="text" placeholder="Search" onKeyUp={props.callbackKeyUp} autoFocus style={{marginRight: 8}}/>
        <Button type="danger" icon="close" onClick={props.handleClose}/>
    </div>
)

FloatSearch.propTypes = {
    callbackKeyUp: PropTypes.func.isRequired,
    handleClose: PropTypes.func.isRequired,
}

export default FloatSearch