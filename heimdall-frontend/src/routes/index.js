import React from 'react'
import { Switch, Redirect } from 'react-router'
// HOCs
import FadeIn from '../components/wrappers/FadeIn'
import AppRoute from '../components/wrappers/AppRouter'

//Layouts
import MainLayout from '../components/layout/MainLayout'
import LoginLayout from '../components/layout/LoginLayout'

// Route components
// import Home from '../components/Home'

import Apis from '../components/apis/Index'
import NewApi from '../components/apis/NewApi'
import SingleApi from '../components/apis/SingleApi'
import SingleListApis from '../components/apis/SingleListApis'
import Monitors from '../components/apis/Monitors'
import ApiInterceptors from '../components/apis/ApiInterceptors'
import SingleResource from '../components/apis/SingleResource'

import LoginContainer from '../containers/Login'
import Environments from '../containers/Environments'

import Authorization from '../components/policy/Authorization'
import SingleEnvironment from '../containers/SingleEnvironment';
import Developers from '../containers/Developers';
import SingleDeveloper from '../containers/SingleDeveloper';
import Apps from '../containers/Apps';
import SingleApp from '../containers/SingleApp';
import Plans from '../containers/Plans';
import SinglePlan from '../containers/SinglePlan';
import AccessTokens from '../containers/AccessTokens';
// import ApiFlows from '../components/apis/ApiFlows';
import SingleAccessToken from '../containers/SingleAccessToken';
import Users from '../containers/Users';
import SingleUser from '../containers/SingleUser';
import Traces from "../containers/Traces";
import SingleTrace from "../containers/SingleTrace";
import SingleApiSwaggerEditor from "../components/apis/SingleApiSwaggerEditor"

const routes = ({ history }) => (
    <Switch>
        <AppRoute layout={MainLayout} history={history} exact path="/" component={Authorization()(FadeIn(Apis))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis" component={Authorization()(FadeIn(Apis))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/new" component={Authorization()(FadeIn(NewApi))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/:id" component={Authorization()(FadeIn(SingleApi))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/:id/swagger-editor" component={Authorization()(FadeIn(SingleApiSwaggerEditor))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/api" component={Authorization()(FadeIn(SingleListApis))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/monitor" component={Authorization()(FadeIn(Monitors))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/interceptors" component={Authorization()(FadeIn(ApiInterceptors))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/resources/:id" component={Authorization()(FadeIn(SingleResource))} />
        <AppRoute layout={LoginLayout} history={history} exact path="/login" component={LoginContainer} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments" component={Authorization()(FadeIn(Environments))} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments/new" component={Authorization()(FadeIn(SingleEnvironment))} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments/:id" component={Authorization()(FadeIn(SingleEnvironment))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers" component={Authorization()(FadeIn(Developers))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers/new" component={Authorization()(FadeIn(SingleDeveloper))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers/:id" component={Authorization()(FadeIn(SingleDeveloper))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps" component={Authorization()(FadeIn(Apps))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps/new" component={Authorization()(FadeIn(SingleApp))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps/:id" component={Authorization()(FadeIn(SingleApp))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans" component={Authorization()(FadeIn(Plans))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans/new" component={Authorization()(FadeIn(SinglePlan))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans/:id" component={Authorization()(FadeIn(SinglePlan))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens" component={Authorization()(FadeIn(AccessTokens))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens/new" component={Authorization()(FadeIn(SingleAccessToken))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens/:id" component={Authorization()(FadeIn(SingleAccessToken))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users" component={Authorization()(FadeIn(Users))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users/new" component={Authorization()(FadeIn(SingleUser))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users/:id" component={Authorization()(FadeIn(SingleUser))} />
        <AppRoute layout={MainLayout} history={history} exact path="/traces" component={Authorization()(FadeIn(Traces))} />
        <AppRoute layout={MainLayout} history={history} exact path="/traces/:id" component={Authorization()(FadeIn(SingleTrace))} />

        {/* routes not finded or 404 */}
        <Redirect to="/" />
        {/* <AppRoute layout={MainLayout} history={history} component={Authorization()(FadeIn(Apis))} /> */}

    </Switch>
)

export default routes
