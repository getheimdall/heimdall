import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import PanelContent from './PanelContent';
import Animate from 'rc-animate';

class HeimdallPanel extends Component {
    handleItemClick() {
        if (this.props.onItemClick) {
            this.props.onItemClick();
        }
    }

    render() {
        const {
            className,
            id,
            style,
            prefixCls,
            header,
            headerClass,
            children,
            isActive,
            showArrow,
            destroyInactivePanel,
            disabled,
            forceRender,
            extra,
            extraClass,
            extraWidth
        } = this.props;
        const headerCls = classNames(`${prefixCls}-header`, {
            [headerClass]: headerClass,
        });
        const extraCls = classNames(`${prefixCls}-extra`, {
            [extraClass]: extraClass,
        });
        const itemCls = classNames({
            [`${prefixCls}-item`]: true,
            [`${prefixCls}-item-active`]: isActive,
            [`${prefixCls}-item-disabled`]: disabled,
        }, className);

        return (
            <div className={itemCls} style={style} id={id} role="tablist">
                <div className={headerCls} onClick={this.handleItemClick.bind(this)} role="tab" aria-expanded={isActive} style={{display: 'inline-block', width: extra ? 100-extraWidth+'%' : '100%' }}>
                    {showArrow && <i className="arrow" />}
                    {header}
                </div>
                
                {extra && <div style={{display: 'inline-block', width: extraWidth+'%'}} className={extraCls}>
                    {extra}
                </div>}
                {/* {extra} */}
                {/* <div style={{display: 'inline-block', width: '20%'}} className={extraCls}>
                    {extra}
                </div> */}
                <Animate showProp="isActive" exclusive component="" animation={this.props.openAnimation} >
                    <PanelContent prefixCls={prefixCls} isActive={isActive} destroyInactivePanel={destroyInactivePanel} forceRender={forceRender}>
                        {children}
                    </PanelContent>
                </Animate>
            </div >
        );
    }
}

HeimdallPanel.propTypes = {
    className: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.object,
    ]),
    id: PropTypes.string,
    children: PropTypes.any,
    openAnimation: PropTypes.object,
    prefixCls: PropTypes.string,
    header: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
        PropTypes.node,
    ]),
    headerClass: PropTypes.string,
    extraClass: PropTypes.string,
    showArrow: PropTypes.bool,
    isActive: PropTypes.bool,
    onItemClick: PropTypes.func,
    style: PropTypes.object,
    destroyInactivePanel: PropTypes.bool,
    disabled: PropTypes.bool,
    forceRender: PropTypes.bool,
    extra: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
        PropTypes.node,
    ]),
    extraWidth: PropTypes.number
};

HeimdallPanel.defaultProps = {
    showArrow: true,
    isActive: false,
    destroyInactivePanel: false,
    onItemClick() { },
    headerClass: '',
    extraClass: '',
    forceRender: false,
    extraWidth: 20
};

export default HeimdallPanel;