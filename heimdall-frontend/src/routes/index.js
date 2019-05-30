import React, { Suspense, lazy } from 'react'
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

import Authorization from '../components/policy/Authorization'
import LoginContainer from '../containers/Login'
import Loading from "../components/ui/Loading";

const Apis = lazy(() => import('../components/apis/Index'))
const NewApi = lazy(() => import('../components/apis/NewApi'))
const SingleApi = lazy(() => import('../components/apis/SingleApi'))
const SingleListApis = lazy(() => import('../components/apis/SingleListApis'))
const ApiInterceptors = lazy(() => import('../components/apis/ApiInterceptors'))
const SingleResource = lazy(() => import('../components/apis/SingleResource'))
const Environments = lazy(() => import('../containers/Environments'))
const SingleEnvironment = lazy(() => import('../containers/SingleEnvironment'))
const Developers = lazy(() => import('../containers/Developers'))
const SingleDeveloper = lazy(() => import('../containers/SingleDeveloper'))
const Apps = lazy(() => import('../containers/Apps'))
const SingleApp = lazy(() => import('../containers/SingleApp'))
const Plans = lazy(() => import('../containers/Plans'))
const SinglePlan = lazy(() => import('../containers/SinglePlan'))
const AccessTokens = lazy(() => import('../containers/AccessTokens'))
const SingleAccessToken = lazy(() => import('../containers/SingleAccessToken'))
const Users = lazy(() => import('../containers/Users'))
const SingleUser = lazy(() => import('../containers/SingleUser'))
const Traces = lazy(() => import('../containers/Traces'))
const SingleTrace = lazy(() => import('../containers/SingleTrace'))
const Roles = lazy(() => import('../containers/Roles'))
const SingleRole = lazy(() => import('../containers/SingleRole'))
const SingleLdap = lazy(() => import('../containers/SingleLdap'))
const Providers = lazy(() => import('../containers/Providers'))
const SingleProvider = lazy(() => import('../containers/SingleProvider'))
const UsersChangePassword = lazy(() => import('../containers/UsersChangePassword'))
const SingleApiSwaggerEditor = lazy(() => import('../components/apis/SingleApiSwaggerEditor'))

const Loadable = Component => props => (
    <Suspense fallback={<div style={{ margin: '0 auto'}}><Loading/></div>}>
            <Component {...props} />
    </Suspense>
)

const routes = ({ history }) => (
        <Switch>
            <AppRoute layout={LoginLayout} history={history} exact path="/login" component={LoginContainer} />
            <AppRoute layout={MainLayout} history={history} exact path="/" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Loadable(Apis)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apis" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Loadable(Apis)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apis/new" component={Authorization([privileges.PRIVILEGE_CREATE_API, privileges.PRIVILEGE_UPDATE_API])(FadeIn(Loadable(NewApi)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apis/:id" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Loadable(SingleApi)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apis/:id/swagger-editor" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Loadable(SingleApiSwaggerEditor)))} />
            <AppRoute layout={MainLayout} history={history} path="/apis/:id/api" component={Authorization([privileges.PRIVILEGE_READ_API])(FadeIn(Loadable(SingleListApis)))} />
            <AppRoute layout={MainLayout} history={history} path="/apis/:id/interceptors" component={Authorization([privileges.PRIVILEGE_READ_API, privileges.PRIVILEGE_READ_INTERCEPTOR])(FadeIn(Loadable(ApiInterceptors)))} />
            <AppRoute layout={MainLayout} history={history} path="/apis/:id/resources/:id" component={Authorization([privileges.PRIVILEGE_READ_API, privileges.PRIVILEGE_READ_RESOURCE])(FadeIn(Loadable(SingleResource)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/environments" component={Authorization([privileges.PRIVILEGE_READ_ENVIRONMENT])(FadeIn(Loadable(Environments)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/environments/new" component={Authorization([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])(FadeIn(Loadable(SingleEnvironment)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/environments/:id" component={Authorization([privileges.PRIVILEGE_READ_ENVIRONMENT])(FadeIn(Loadable(SingleEnvironment)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/developers" component={Authorization([privileges.PRIVILEGE_READ_DEVELOPER])(FadeIn(Loadable(Developers)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/developers/new" component={Authorization([privileges.PRIVILEGE_CREATE_DEVELOPER, privileges.PRIVILEGE_UPDATE_DEVELOPER])(FadeIn(Loadable(SingleDeveloper)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/developers/:id" component={Authorization([privileges.PRIVILEGE_READ_DEVELOPER])(FadeIn(Loadable(SingleDeveloper)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apps" component={Authorization([privileges.PRIVILEGE_READ_APP])(FadeIn(Loadable(Apps)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apps/new" component={Authorization([privileges.PRIVILEGE_CREATE_APP, privileges.PRIVILEGE_UPDATE_APP])(FadeIn(Loadable(SingleApp)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/apps/:id" component={Authorization([privileges.PRIVILEGE_READ_APP])(FadeIn(Loadable(SingleApp)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/plans" component={Authorization([privileges.PRIVILEGE_READ_PLAN])(FadeIn(Loadable(Plans)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/plans/new" component={Authorization([privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN])(FadeIn(Loadable(SinglePlan)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/plans/:id" component={Authorization([privileges.PRIVILEGE_READ_PLAN])(FadeIn(Loadable(SinglePlan)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/tokens" component={Authorization([privileges.PRIVILEGE_READ_ACCESSTOKEN])(FadeIn(Loadable(AccessTokens)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/tokens/new" component={Authorization([privileges.PRIVILEGE_CREATE_ACCESSTOKEN, privileges.PRIVILEGE_UPDATE_ACCESSTOKEN])(FadeIn(Loadable(SingleAccessToken)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/tokens/:id" component={Authorization([privileges.PRIVILEGE_READ_ACCESSTOKEN])(FadeIn(Loadable(SingleAccessToken)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/users" component={Authorization([privileges.PRIVILEGE_READ_USER])(FadeIn(Loadable(Users)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/users/change-password" component={FadeIn(Loadable(UsersChangePassword))} />
            <AppRoute layout={MainLayout} history={history} exact path="/users/new" component={Authorization([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])(FadeIn(Loadable(SingleUser)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/users/:id" component={Authorization([privileges.PRIVILEGE_READ_USER])(FadeIn(Loadable(SingleUser)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/traces" component={Authorization([privileges.PRIVILEGE_READ_TRACES])(FadeIn(Loadable(Traces)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/traces/:id" component={Authorization([privileges.PRIVILEGE_READ_TRACES])(FadeIn(Loadable(SingleTrace)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/roles" component={Authorization([privileges.PRIVILEGE_READ_ROLE])(FadeIn(Loadable(Roles)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/roles/new" component={Authorization([privileges.PRIVILEGE_READ_ROLE])(FadeIn(Loadable(SingleRole)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/roles/:id" component={Authorization([privileges.PRIVILEGE_READ_ROLE])(FadeIn(Loadable(SingleRole)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/ldap" component={Authorization([privileges.PRIVILEGE_READ_LDAP])(FadeIn(Loadable(SingleLdap)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/providers" component={Authorization([privileges.PRIVILEGE_READ_PROVIDER])(FadeIn(Loadable(Providers)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/providers/new" component={Authorization([privileges.PRIVILEGE_READ_PROVIDER, privileges.PRIVILEGE_CREATE_PROVIDER])(FadeIn(Loadable(SingleProvider)))} />
            <AppRoute layout={MainLayout} history={history} exact path="/providers/:id" component={Authorization([privileges.PRIVILEGE_READ_PROVIDER])(FadeIn(Loadable(SingleProvider)))} />
            {/* routes not found or 404 */}
            <Redirect to="/" />
        </Switch>
)

export default routes
