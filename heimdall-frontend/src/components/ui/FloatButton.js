import React from 'react'
import PropTypes from 'prop-types'
import { Button, Tooltip } from 'antd'

const buttonStyle = {
    position: 'fixed',
    bottom: '30px',
    right: '30px',
    zIndex: 9
}

const FloatButton = ({label, onClick, idButton}) => (
    <Tooltip placement="left" title={label}>
        <Button id={idButton} style={buttonStyle} className="floatButton" type="primary" icon="plus" onClick={onClick} size="large" shape="circle" />
    </Tooltip>
)

FloatButton.propTypes = {
    label: PropTypes.string,
    onClick: PropTypes.func.isRequired,
    idButton: PropTypes.string.isRequired
}

export default FloatButton