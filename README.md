[![Build Status](https://travis-ci.org/getheimdall/heimdall.svg?branch=master)](https://travis-ci.org/getheimdall/heimdall)
[![Github All Releases](https://img.shields.io/github/downloads/getheimdall/heimdall/total.svg)](https://github.com/getheimdall/heimdall)

<p align="center"> 
  <img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/heimdall-logo.png">
</p>
<p align="center">
  An easy way to orchestrate your APIs
</p>


## What is Heimdall

Heimdall is an API orchestrator, providing a simple way to manipulate request/response and uncoupling it from your business domain, granting more flexibility and customization to your API.

<p align="center"> 
<img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/screenshot-api-index.png" width="160px" title="List of API">	
<img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/screenshot-api-detail.png" width="160px" title="API detail">

	
  <img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/screenshot-login.png" width="160px" title="Login Page">
  
<img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/screenshot-api-interceptores.png" width="160px" title="Create interceptors">
<img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/screenshot-api-resources.png" width="160px" title="Create resources">
</p>

### Features
* Gateway
	GET; POST; PUT; DELETE; PATH; HEAD
* Dashboards
* Analytics
* Interceptors
   * Mock
   * Logging
   * Rating limit
   * Security
   * Customization with Java or Groovy
* Middlewares
* Security
   * Authentication
   * Authorization

## Usage
Keep in mind you are going to start 3 Spring Boot applications, 1 Database instance and RabbitMq. Make sure you have `4 GB` RAM available on your machine.

### First, clone the project

```sh 
$ git clone --depth=1 https://github.com/getheimdall/heimdall.git heimdall
$ cd heimdall
```

### Docker mode

#### Requirements
- Docker https://www.docker.com/
- Docker Compose https://docs.docker.com/compose/

If you would like to build images yourself (with some changes in the code, for example), you will have to clone all repository and build artifacts with Maven. Then, run: 

```sh
$ docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

`docker-compose.dev.yml` inherits `docker-compose.yml` with additional possibility to build images locally and expose all containers ports for convenient development.

Access Heimdall on: [http://localhost:3000](http://localhost:3000)

### Developer mode

### Requirements
- Nodejs https://nodejs.org/
- Maven https://maven.apache.org/
- Java 8 http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- PostgreSQL https://www.postgresql.org/
- Erlang http://www.erlang.org/downloads
- RabbitMQ https://www.rabbitmq.com/download.html
- Redis https://redis.io/download or https://github.com/MicrosoftArchive/redis/releases

Open your favorite Terminal and run these commands.

To start config:

```sh
$ cd /heimdall-config
$ mvn spring-boot:run
```

To start gateway (requires **CONFIG** already started): 

```sh
$ cd /heimdall-gateway
$ mvn spring-boot:run
```

Third, tab to start API (requires **CONFIG** already started):

```sh
$ cd /heimdall-api
$ mvn spring-boot:run
```

[optional] To start front-end (requires **API** already started):

With Yarn:
```sh
$ cd /heimdall-frontend
$ yarn
$ yarn run
```

Without Yarn:
```sh
$ cd /heimdall-frontend
$ npm install
$ npm run start
```

### Important endpoints
- http://localhost:8080 - Gateway
- http://localhost:8888 - Config
- http://localhost:9090 - API
- http://localhost:3000 - Front-end

### Notes
All Spring Boot applications require an already running [Config Server](https://github.com/sqshq/PiggyMetrics#config-service) for startup. But we can start all containers simultaneously because of `depends_on` docker-compose option.


## Getting Started

To create the routing process, there is a specific registration process that has to be followed. This documentation will provide all information needed to complete the process through a step-by-step guide.

### Environment

The first verification from Heimdall is the environment linked in the request. For an API be registered, it must have a linked environment. The environment is the heart of the routing, with which Heimdall will indentify both the origin and destiny routes. One API can be linked with more than one environment; it is your choice.

The primal step before registering an API is to define an environment. To do that, use the menu under *Menu -> Environment -> Add* to start an environment registration.

### API

Once there is a registered environment, it is possible to register an API. For that, use the API menu. During the API's registration process, its identification will be the "basepath". An API is a resource group; this resources can be added after the API registration is finished.

### Resources

The resource is nothing more than one operation group. Its use is to organize the operations (that will be explained in the next topic). The resource registration can be initialized after an API is added. The resource was created to organize the operations and make it easier to link the interceptors to a group.

Example: User domain; the resource name will be "users"
- POST - /basePath/users
- GET - /basePath/users/{id}
- PUT - /basePath/users/{id}
- DELETE - /basePath/users/{id}

A resource can have any name, but to make this example simpler, we will use the names that make sense about the operations used in this documentation.

### Operations

To complete the basic lifecycle in order to use the API, it is necessary to add some operations. The operation is a real service the gateway will route. A group of operations comprise an API. They are responsable to map the back-end services that will be called. As an example, we used the same paths as in the resources topic.

Example: User domain; the resource name will be "users"
- **POST** - /basePath/**users**
- **GET** - /basePath/**users/{id}**
- **PUT** - /basePath/**users/{id}**
- **DELETE** - /basePath/**users/{id}**

The bolded portions above are operations. An operation needs a HTTP verb to be unique.

### Plans

The plan is just a tag to the API. Its main function is to link some resources that Heimdall have without impacting the API directly, making possible for multiple API consumers to have their respective configurations without one affecting the other.

For instance, think of two clients consuming the same API, but one of them wants to save the request logs and the other does not. To enable this, one client will have a "Plan A" and the other will have a "Plan B", where "Plan A" will be linked with an "Interceptor Log" and "Plan B" will not. This way, both will be consuming the same service without affecting each other.

### Interceptors

With Heimdall, you can manipulate the requests through the **interceptors**. You can intercept requests in lots of situations, like block access to some services or just save the request logs. By default, Heimdall already has some interceptors available.

To create an interceptor, an API needs to exist. When you select an API, you can see an "Interceptor" tab to manipulate or add a new interceptor. Using drag n' drop, it is possible to add or remove an interceptor where you wish. Drag an interceptor to the request or response areas and a dialog will be shown for you to submit an interceptor with its details.

**It is important to note that you cannot add an interceptor directly to an API; you can attach it to a Plan, Resource or Operation that are registered to that API.**

### Developers

Heimdall being an orchestrator, you have lots of managers, and one of their responsibilities is to govern the developers using the API. A developer can use registered apps to consume the API services. To manage the developers, you just need to navigate to developers menu; there you can add a new developer or manipulate the registered developers.

### Apps

The developer is able to own multiple apps inside Heimdall. An app represents an external application that the developer wants to register in Heimdall. The app can consume any API registered in Heimdall. Every app created has its own 'Client ID'; this 'Client ID', in most cases, is used to identify the app in the request.

### Access Tokens

'Access tokens' are tokens used to provide basic access through Heimdall. They are linked to an app, and to turn on the validation it is necessary you add a 'Access Token Interceptor'.

**It is important to know that Access Token Interceptor requires a Client ID Interceptor**

## Infrastructure
Heimdall is composed of six modules, where three of them are central to the project — **Config**, **API** and **Gateway** —, being built around certain business domains.

### Config
[Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) is a horizontally scalable centralized configuration service for distributed systems. It uses a pluggable repository layer that currently supports local storage, Git, and Subversion. 

In this project, we use `native profile` which simply loads config files from the local classpath. You can look into `shared` directory in [Config service resources](https://ourGitHub...). Now, when Heimdall-api requests its configuration, Config service responds with `shared/application-api.yml`.

### Gateway
We built our algorithm on top of [Netflix Zuul](https://github.com/Netflix/zuul). We put some steroids on the Zuul Filters and added some criterias to make the routes matching more rigid (like the **HTTP Verb** on the match). Plus, to manage the request/response and Zuul Filters flow, we applied a message broker ([RabbitMQ](https://www.rabbitmq.com/)) to communicate with the API.

### API
To present an easy way to manage the gateway, we implemented an API to make it easier to add new routes, interceptors, rate limit and others things to manipulate the gateway at runtime.

## Deploy on Heroku

Heimdall can be easily deployed to Heroku clicking on this button:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

or executing the commands:

```sh
$ heroku login
$ heroku create
$ git push heroku master
$ heroku open
```

## Feedback welcome

Heimdall is open source, and we appreciate your help. Feel free to contribute.

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the owners of this repository before making a change.

Please note we have a code of conduct, please follow it in all your interactions with the project.

## Pull Request Process

1. Ensure any install or build dependencies are removed before the end of the layer when doing a 
   build.
2. Update the README.md with details of changes to the interface. This includes new environment 
   variables, exposed ports, useful file locations and container parameters.
3. Increase the version numbers in any examples files and the README.md to the new version that this
   Pull Request would represent. The versioning scheme we use is [SemVer](http://semver.org/).
4. You may merge the Pull Request in once you have the sign-off of two other developers, or if you 
   do not have permission to do that, you may request the second reviewer to merge it for you.


## Roadmap

**Only Heimdall subteam members should create new issues in this repo**. If you believe a project should be added within the tracker, please leave a comment on the appropriate "parent" issue, i.e., one of the issues linked below. And, in general, feel free to use comments to ask questions, pitch ideas, or mention updates that need to be made!

There are issues for each of the vision statements:

* [Heimdall should have Metrics](https://github.com/getheimdall/issue/...)
* [Heimdall should have Dashboard](https://github.com/getheimdall/issue/...)
* [Heimdall should provide a easy way to track the request/response](https://github.com/getheimdall/issue/...)
* [Heimdall should have a editor to interceptors](https://github.com/getheimdall/issue/...)
* [Heimdall should provide a solid way to test middlewares](https://github.com/getheimdall/issue/...)
