import React from 'react'
import { Switch, Redirect } from 'react-router'
// HOCs
import FadeIn from '../components/wrappers/FadeIn'
import AppRoute from '../components/wrappers/AppRouter'

//Layouts
import MainLayout from '../components/layout/MainLayout'
import LoginLayout from '../components/layout/LoginLayout'

//import privileges
import {
    PRIVILEGE_CREATE_ACCESSTOKEN,
    PRIVILEGE_CREATE_API,
    PRIVILEGE_CREATE_APP,
    PRIVILEGE_CREATE_DEVELOPER,
    PRIVILEGE_CREATE_ENVIRONMENT,
    PRIVILEGE_CREATE_PLAN, PRIVILEGE_CREATE_USER, PRIVILEGE_READ_ACCESSTOKEN,
    PRIVILEGE_READ_API,
    PRIVILEGE_READ_APP,
    PRIVILEGE_READ_DEVELOPER,
    PRIVILEGE_READ_ENVIRONMENT,
    PRIVILEGE_READ_INTERCEPTOR,
    PRIVILEGE_READ_PLAN,
    PRIVILEGE_READ_RESOURCE, PRIVILEGE_READ_TRACES, PRIVILEGE_READ_USER, PRIVILEGE_UPDATE_ACCESSTOKEN,
    PRIVILEGE_UPDATE_API,
    PRIVILEGE_UPDATE_APP,
    PRIVILEGE_UPDATE_DEVELOPER,
    PRIVILEGE_UPDATE_ENVIRONMENT, PRIVILEGE_UPDATE_PLAN, PRIVILEGE_UPDATE_USER
} from '../utils/ConstantsPrivileges'

// Route components
// import Home from '../components/Home'

import Apis from '../components/apis/Index'
import NewApi from '../components/apis/NewApi'
import SingleApi from '../components/apis/SingleApi'
import SingleListApis from '../components/apis/SingleListApis'
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

const routes = ({ history }) => (
    <Switch>
        <AppRoute layout={MainLayout} history={history} exact path="/" component={Authorization([PRIVILEGE_READ_API]) (FadeIn(Apis))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis" component={Authorization([ PRIVILEGE_READ_API]) (FadeIn(Apis))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/new" component={Authorization([ PRIVILEGE_CREATE_API, PRIVILEGE_UPDATE_API ])(FadeIn(NewApi))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/:id" component={Authorization([ PRIVILEGE_READ_API ])(FadeIn(SingleApi))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/api" component={Authorization([ PRIVILEGE_READ_API ])(FadeIn(SingleListApis))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/interceptors" component={Authorization([PRIVILEGE_READ_API, PRIVILEGE_READ_INTERCEPTOR])(FadeIn(ApiInterceptors))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/resources/:id" component={Authorization([PRIVILEGE_READ_API, PRIVILEGE_READ_RESOURCE])(FadeIn(SingleResource))} />
        <AppRoute layout={LoginLayout} history={history} exact path="/login" component={LoginContainer} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments" component={Authorization([PRIVILEGE_READ_ENVIRONMENT])(FadeIn(Environments))} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments/new" component={Authorization([PRIVILEGE_CREATE_ENVIRONMENT, PRIVILEGE_UPDATE_ENVIRONMENT])(FadeIn(SingleEnvironment))} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments/:id" component={Authorization([PRIVILEGE_READ_ENVIRONMENT])(FadeIn(SingleEnvironment))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers" component={Authorization([PRIVILEGE_READ_DEVELOPER])(FadeIn(Developers))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers/new" component={Authorization([PRIVILEGE_CREATE_DEVELOPER, PRIVILEGE_UPDATE_DEVELOPER])(FadeIn(SingleDeveloper))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers/:id" component={Authorization([PRIVILEGE_READ_DEVELOPER])(FadeIn(SingleDeveloper))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps" component={Authorization([PRIVILEGE_READ_APP])(FadeIn(Apps))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps/new" component={Authorization([PRIVILEGE_CREATE_APP, PRIVILEGE_UPDATE_APP])(FadeIn(SingleApp))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps/:id" component={Authorization([PRIVILEGE_READ_APP])(FadeIn(SingleApp))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans" component={Authorization([PRIVILEGE_READ_PLAN])(FadeIn(Plans))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans/new" component={Authorization([PRIVILEGE_CREATE_PLAN, PRIVILEGE_UPDATE_PLAN])(FadeIn(SinglePlan))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans/:id" component={Authorization([PRIVILEGE_READ_PLAN])(FadeIn(SinglePlan))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens" component={Authorization([PRIVILEGE_READ_ACCESSTOKEN])(FadeIn(AccessTokens))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens/new" component={Authorization([PRIVILEGE_CREATE_ACCESSTOKEN, PRIVILEGE_UPDATE_ACCESSTOKEN])(FadeIn(SingleAccessToken))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens/:id" component={Authorization([PRIVILEGE_READ_ACCESSTOKEN])(FadeIn(SingleAccessToken))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users" component={Authorization([PRIVILEGE_READ_USER])(FadeIn(Users))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users/new" component={Authorization([PRIVILEGE_CREATE_USER, PRIVILEGE_UPDATE_USER])(FadeIn(SingleUser))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users/:id" component={Authorization([PRIVILEGE_READ_USER])(FadeIn(SingleUser))} />
        <AppRoute layout={MainLayout} history={history} exact path="/traces" component={Authorization([PRIVILEGE_READ_TRACES])(FadeIn(Traces))} />
        <AppRoute layout={MainLayout} history={history} exact path="/traces/:id" component={Authorization([PRIVILEGE_READ_TRACES])(FadeIn(SingleTrace))} />

        {/* routes not finded or 404 */}
        <Redirect to="/" />
        {/* <AppRoute layout={MainLayout} history={history} component={Authorization('TESTE)(FadeIn(Apis))} /> */}

    </Switch>
)

export default routes
