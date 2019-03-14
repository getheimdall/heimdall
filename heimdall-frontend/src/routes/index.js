import React from 'react'
import { Switch, Redirect } from 'react-router'
// HOCs
import FadeIn from '../components/wrappers/FadeIn'
import AppRoute from '../components/wrappers/AppRouter'

//Layouts
import MainLayout from '../components/layout/MainLayout'
import LoginLayout from '../components/layout/LoginLayout'

//import privileges
import { privileges } from '../constants/privileges-types'

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
import SingleAccessToken from '../containers/SingleAccessToken';
import Users from '../containers/Users';
import SingleUser from '../containers/SingleUser';
import Traces from "../containers/Traces";
import SingleTrace from "../containers/SingleTrace";
import Roles from "../containers/Roles";
import SingleRole from "../containers/SingleRole";
import SingleLdap from "../containers/SingleLdap";
import Providers from "../containers/Providers";
import SingleProvider from "../containers/SingleProvider";

const routes = ({ history }) => (
    <Switch>
        <AppRoute layout={MainLayout} history={history} exact path="/" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Apis))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Apis))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/new" component={Authorization([privileges.PRIVILEGE_CREATE_API, privileges.PRIVILEGE_UPDATE_API])(FadeIn(NewApi))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apis/:id" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(SingleApi))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/api" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(SingleListApis))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/interceptors" component={Authorization([privileges.PRIVILEGE_READ_API, privileges.PRIVILEGE_READ_INTERCEPTOR])(FadeIn(ApiInterceptors))} />
        <AppRoute layout={MainLayout} history={history} path="/apis/:id/resources/:id" component={Authorization([privileges.PRIVILEGE_READ_API, privileges.PRIVILEGE_READ_RESOURCE])(FadeIn(SingleResource))} />
        <AppRoute layout={LoginLayout} history={history} exact path="/login" component={LoginContainer} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments" component={Authorization([privileges.PRIVILEGE_READ_ENVIRONMENT])(FadeIn(Environments))} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments/new" component={Authorization([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])(FadeIn(SingleEnvironment))} />
        <AppRoute layout={MainLayout} history={history} exact path="/environments/:id" component={Authorization([privileges.PRIVILEGE_READ_ENVIRONMENT])(FadeIn(SingleEnvironment))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers" component={Authorization([privileges.PRIVILEGE_READ_DEVELOPER])(FadeIn(Developers))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers/new" component={Authorization([privileges.PRIVILEGE_CREATE_DEVELOPER, privileges.PRIVILEGE_UPDATE_DEVELOPER])(FadeIn(SingleDeveloper))} />
        <AppRoute layout={MainLayout} history={history} exact path="/developers/:id" component={Authorization([privileges.PRIVILEGE_READ_DEVELOPER])(FadeIn(SingleDeveloper))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps" component={Authorization([privileges.PRIVILEGE_READ_APP])(FadeIn(Apps))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps/new" component={Authorization([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])(FadeIn(SingleApp))} />
        <AppRoute layout={MainLayout} history={history} exact path="/apps/:id" component={Authorization([privileges.PRIVILEGE_READ_APP])(FadeIn(SingleApp))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans" component={Authorization([privileges.PRIVILEGE_READ_PLAN])(FadeIn(Plans))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans/new" component={Authorization([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])(FadeIn(SinglePlan))} />
        <AppRoute layout={MainLayout} history={history} exact path="/plans/:id" component={Authorization([privileges.PRIVILEGE_READ_PLAN])(FadeIn(SinglePlan))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens" component={Authorization([privileges.PRIVILEGE_READ_ACCESSTOKEN])(FadeIn(AccessTokens))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens/new" component={Authorization([privileges.PRIVILEGE_CREATE_ACCESSTOKEN, privileges.PRIVILEGE_UPDATE_ACCESSTOKEN])(FadeIn(SingleAccessToken))} />
        <AppRoute layout={MainLayout} history={history} exact path="/tokens/:id" component={Authorization([privileges.PRIVILEGE_READ_ACCESSTOKEN])(FadeIn(SingleAccessToken))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users" component={Authorization([privileges.PRIVILEGE_READ_USER])(FadeIn(Users))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users/new" component={Authorization([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])(FadeIn(SingleUser))} />
        <AppRoute layout={MainLayout} history={history} exact path="/users/:id" component={Authorization([privileges.PRIVILEGE_READ_USER])(FadeIn(SingleUser))} />
        <AppRoute layout={MainLayout} history={history} exact path="/traces" component={Authorization([privileges.PRIVILEGE_READ_TRACES])(FadeIn(Traces))} />
        <AppRoute layout={MainLayout} history={history} exact path="/traces/:id" component={Authorization([privileges.PRIVILEGE_READ_TRACES])(FadeIn(SingleTrace))} />
        <AppRoute layout={MainLayout} history={history} exact path="/roles" component={Authorization([privileges.PRIVILEGE_READ_ROLE])(FadeIn(Roles))} />
        <AppRoute layout={MainLayout} history={history} exact path="/roles/new" component={Authorization([privileges.PRIVILEGE_READ_ROLE])(FadeIn(SingleRole))} />
        <AppRoute layout={MainLayout} history={history} exact path="/roles/:id" component={Authorization([privileges.PRIVILEGE_READ_ROLE])(FadeIn(SingleRole))} />
        <AppRoute layout={MainLayout} history={history} exact path="/ldap" component={Authorization([privileges.PRIVILEGE_READ_LDAP])(FadeIn(SingleLdap))} />
        <AppRoute layout={MainLayout} history={history} exact path="/providers" component={Authorization([privileges.PRIVILEGE_READ_PROVIDER])(FadeIn(Providers))} />
        <AppRoute layout={MainLayout} history={history} exact path="/providers/new" component={Authorization([privileges.PRIVILEGE_READ_PROVIDER, privileges.PRIVILEGE_CREATE_PROVIDER])(FadeIn(SingleProvider))} />
        <AppRoute layout={MainLayout} history={history} exact path="/providers/:id" component={Authorization([privileges.PRIVILEGE_READ_PROVIDER])(FadeIn(SingleProvider))} />
        {/* routes not finded or 404 */}
        <Redirect to="/" />

    </Switch>
)

export default routes
