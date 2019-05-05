import React from 'react'
import PropTypes from 'prop-types'
import { Icon, Row, Col, Checkbox, Menu, Dropdown, Button } from 'antd'

const PageTitle = ({ title, icon, items, handleCard, IconComponent }) => {
    return (
        <Row className="page-header" style={{ padding: '0 30px' }}>
            <Col sm={12} style={{ opacity: 0.7 }}>
                <h2 style={{ color: '#0a183f' }}>{title} {!IconComponent && <Icon type={icon} />} {IconComponent && <IconComponent width={26} height={26}/>}</h2>
            </Col>
            <Col sm={12} style={{ textAlign: 'right' }}>
                {
                    items
                        ? (
                            <Dropdown overlay={(
                                <Menu>
                                    {items.map((item, index) => (
                                        <Menu.Item key={index}>
                                            <Checkbox
                                                onChange={handleCard}
                                                defaultChecked={true}
                                                value={item.id}
                                            >
                                                {item.title}
                                            </Checkbox>
                                        </Menu.Item>
                                    ))}
                                </Menu>
                            )} trigger={['click']}>
                                <Button type="primary" className="ant-dropdown-link">
                                    <Icon type="eye-o" /> <Icon type="down" />
                                </Button>
                            </Dropdown>
                        )
                        : null
                }
            </Col>
        </Row>
    )
}

PageTitle.propTypes = {
    title: PropTypes.string.isRequired,
    icon: PropTypes.string,
    items: PropTypes.array,
    handleCard: PropTypes.func
}

export default PageTitle

