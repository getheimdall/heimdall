import React, { PureComponent } from 'react'
import nprogress from 'nprogress'
import ReactCSSTransitionGroup from 'react-addons-css-transition-group'
import Breadcrumb from '../ui/Breadcrumb'

const FadeInHOC = (WrappedComponent) => {
    class FadeIn extends PureComponent {
        componentWillMount() {
            nprogress.start()
        }

        componentDidMount() {
            nprogress.done()
        }

        render() {
            return (
                <div>
                    <Breadcrumb pathName={this.props.history.location.pathname} />
                    <ReactCSSTransitionGroup
                        transitionName="fade"
                        transitionAppear={true}
                        transitionAppearTimeout={500}
                        transitionEnter={false}
                        transitionLeave={false}
                    >
                        <WrappedComponent {...this.props} />
                    </ReactCSSTransitionGroup>
                </div>
            )
        }
    }

    return FadeIn
}

export default FadeInHOC