import React from 'react'
import PropTypes from 'prop-types'
import { Row, Breadcrumb } from 'antd'
import { Link } from 'react-router-dom'

const PageBreadcrumb = ({pathName}) => {
    let arr = pathName.split('/')
    arr = arr.filter(item => item !== "")
    return (
        <Row className="h-breadcrumb">
            <Breadcrumb style={{ textAlign: 'right' }}>
                <Breadcrumb.Item key="home"><Link to="/">Home</Link></Breadcrumb.Item>
                {arr && arr.length > 0
                    ? arr.map((item, i) => {
                        let link = ''
                        arr.forEach((it, j) => {
                            if (j <= i) {
                                link += '/' + it
                            } else {
                                return
                            }
                        })

                        return (<Breadcrumb.Item key={i} style={{ textTransform: 'capitalize' }} ><Link to={link}>{item}</Link></Breadcrumb.Item>)
                    })
                    : null
                }
            </Breadcrumb>
        </Row>
    )
}

PageBreadcrumb.propTypes = {
    pathName: PropTypes.string.isRequired
}

export default PageBreadcrumb

