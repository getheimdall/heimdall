# Heimdall

<p align="center"> 
  <img src="https://raw.githubusercontent.com/getheimdall/heimdall/master/.github/heimdall-logo.png">
</p>
<p align="center">
  An easy way to orchestrate your Api's
</p>


## What's Heimdall

This project is an easy way to use an API Orchestrator to your project. A simple way to manipulate request/response and uncouple your business domain, providing more flexibilities and personalize your API.

### Features
- imagem 1
- imagem 2
- imagem 3 completa.

## Infrastructure
Heimdall was decomposed into six modules where three of they are principal (**Config, Api, Gateway**), built in around certain business domains.

### Config
[Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) is horizontally scalable centralized configuration service for distributed systems. It uses a pluggable repository layer that currently supports local storage, Git, and Subversion. 

In this project, We use `native profile`, which simply loads config files from the local classpath. You can see `shared` directory in [Config service resources](https://ourGitHub...). Now, when Heimdall-api requests it's configuration, Config service responses with `shared/application-api.yml`.

### Gateway
In this project we built our algorithm over the [Netflix Zuul](https://github.com/Netflix/zuul). We put some steroids on the Zuul Filters, add some criterias to make the match routes more rigid (like the **HTTP Verb** on the match). And to manager the request/response and Zuul Filters flow we put a message broker ([RabbitMQ](https://www.rabbitmq.com/)) to communicate with the API.

### Api
To provide a easy way to manage the gateway we put an Api to make more easy add new routes, interceptors, rate limit and others things to manipulate the gateway at runtime.

## Usage
Keep in mind, that you are going to start 3 Spring Boot applications, 1 Database instance and RabbitMq. Make sure you have `4 Gb` RAM available on your machine.\

### Requirements
- Docker
- Docker Compose
- Nodejs
- Maven
- Java 8

### First clone the project

```sh 
$ git clone --depth=1 https://github.com/getheimdall/heimdall.git heimdall
$ cd heimdall
```

### Docker mode
...

If you'd like to build images yourself (with some changes in the code, for example), you have to clone all repository and build artifacts with maven. Then, run: 

```sh
$ docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

`docker-compose.dev.yml` inherits `docker-compose.yml` with additional possibility to build images locally and expose all containers ports for convenient development.

Access Heimdal on: [http://localhost:3000](http://localhost:3000)

### Deploy on Heroku

Heimdall app can easily be deployed to Heroku clicking on button: 

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

or executing the commands:

```sh
$ heroku login
$ heroku create
$ git push heroku master
$ heroku open
```

### Running locally

Open your favorite Terminal and run these commands.

First tab to start config:

```sh
$ cd /heimdall-config
$ mvn spring-boot:run
```

Second tab to start gateway (require **CONFIG** alredy started): 

```sh
$ cd /heimdall-gateway
$ mvn spring-boot:run
```

Third tab to start api (require **CONFIG** alredy started):

```sh
$ cd /heimdall-api
$ mvn spring-boot:run
```

(optional) Fourth tab to start front-end (require **API** alredy started)

With Yarn
```sh
$ cd /heimdall-frontend
$ yarn
$ yarn run
```

Withou Yarn
```sh
$ cd /heimdall-frontend
$ npm install
$ npm run start
```

### Important endpoints
- http://localhost:8080 - Gateway
- http://localhost:8888 - Config
- http://localhost:9090 - Api
- http://localhost:3000 - frontend

### Notes
All Spring Boot applications require already running [Config Server](https://github.com/sqshq/PiggyMetrics#config-service) for startup. But we can start all containers simultaneously because of `depends_on` docker-compose option.

## Feedback welcome
Heimdall is open source, and we appreciate your help. Feel free to contribute.

## Roadmap

**Only Heimdall subteam members should create new issues in this repo**. If you
believe a project should be added within the tracker, please leave a comment on
the appropriate "parent" issue, i.e. one of the issues linked below. And in
general, feel free to use comments to ask questions, pitch ideas, or mention
updates that need to be made!

There are issues for each of the vision statements:

* [Heimdall should have Metrics](https://github.com/getheimdall/issue/...)
* [Heimdall should have Dashboard](https://github.com/getheimdall/issue/...)
* [Heimdall should provide a easy way to track the request/response](https://github.com/getheimdall/issue/...)
* [Heimdall should have a editor to interceptors](https://github.com/getheimdall/issue/...)
* [Heimdall should provide a solid way to test middlewares](https://github.com/getheimdall/issue/...)
