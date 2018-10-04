import React from 'react'
import logo from '../../icon-heimdall.png'

const Logo = ({history, collapsed}) => (
    <div className="logo"
        style={{
            height: 66,
            padding: '0 20px',
            lineHeight: '58px',
            textAlign: 'center',
            width: !collapsed ? '200px' : '80px'
        }}
        onClick={() => history.push('/')}
    >
        <img src={logo}
            style={{
                width: 'auto',
                height: 40,
                display: 'inline-block'
            }}
            alt="Heimdall"
        />
         {!collapsed && <span>HEIMDALL</span>}
        {/* <small>API Gateway</small> */}
    </div>
)

export default Logo