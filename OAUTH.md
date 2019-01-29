# OAuth 2.0 Heimdall

O Heimdall suporta oficialmente dois fluxos da especificação OAuth 2.0: _Password_ e _Implicit_

## Table of Contents

* Requisitos iniciais
    * Criação do Plan
    * Criação do App
    * Provider
        * Criação do Provider
        * Utilização do Provider

* Endpoints de autorização
    * Criação do endpoint
    * Adição do Interceptor

* Criação da validação

* Fluxos
    * Password Flow
    * Refresh Flow
    * Implicit Flow

## Requisitos Iniciais

Para o OAuth fazer o seu trabalho uma certa estrutura deve ser criada. Um App deve ser criado no Heimdall para consumir a Api. A forma de ligar um App a uma Api é através de um Plan, como mostrado no diagrama abaixo.

![Schema](docs/assets/img/oauth/schema.png)

### Criação do Plan

Um Plan deve estar vinculado a uma Api. Sendo assim cadastre um plano e o associe a sua Api.


### Criação do App

Após a criação do Plan, crie um App e selecione o seu Plan criado. No App será gerado o ClientID que será utilizado nos fluxos de autorização.

### Provider

Um Provider é um serviço REST de autorização, é ele que irá verificar as credenciais do usuário para autorização de acesso aos recusos protegidos. Sempre será feita uma chamada do tipo POST a esse serviço com as configurações informadas no cadastro do Provider.
 
 Por enquanto o cadastro de um Provider ainda não está disponível no front end, sendo assim o cadastro ainda é feito pelo Swagger. Para isso acesse `<url_do_heimdall_api>:9090/v1/index.html` e logue com as credenciais de administrador. Procure a seção de Provider.

#### Criação do Provider

Nesse caso vamos mostrar como criar um Provider que espera dois campos chamdos "username" e "passoword" serem enviados no body da requisição para realizar a autorização. Selecione a opção "Save a new provider".

![Provider](docs/assets/img/oauth/provider.png)

Essa opção será um POST contendo um json com as informações do seu Provider como mostrado abaixo.

```json
{
  "description": "A Simple Provider with UserName and Password",
  "name": "My Provider",
  "path": "<provider_url>",
  "providerParams": [
    {
      "location": "BODY",
      "name": "username",
      "value": ""
    },
    {
      "location": "BODY",
      "name": "password",
      "value": ""
    }
  ]
}
```

`descrition`: uma descrição simples do seu Provider

`name`: o nome do seu provider

`path`: URL do seu Provider (serviço REST de autorização)

`providerParams`: A lista de parâmetros que seu serviço de autorização irá precisar para fazer a autenticação do usuário. Cada parâmetro tem a sua lista de opções.

`providerParams.location`: local onde a informação será enviada para o serviço de autorização. As opções são HEADER ou BODY, caso não seja informado será enviado via queryParams.

`providerParams.name`: nome do parâmetro
 
`providerParams.value`: valor padrão, deixe em branco para utilizar o valor enviado na chamada

#### Utilização do Provider

Para utilizar o seu Provider criado você irá precisar do seu id. Para ter acesso a essa informação selecione a opção `GET -  Find all providers with filter and pageable` e faça uma busca utilizando o mesmo nome do provider que você criou. Uma resposta como essa deverá aparecer:

```json
[
  {
    "id": 1,
    "name": "My Provider",
    "description": "A Simple Provider with UserName and Password",
    "path": "<provider_url>",
    "providerParams": [
      {
        "id": 1,
        "name": "password",
        "location": "BODY",
        "value": "",
        "creationDate": "2020-01-01T12:00:00.000",
        "status": "ACTIVE"
      },
      {
        "id": 2,
        "name": "username",
        "location": "BODY",
        "value": "",
        "creationDate": "2020-01-01T12:00:00.000",
        "status": "ACTIVE"
      }
    ],
    "creationDate": "2020-01-01T12:00:00.000",
    "status": "ACTIVE"
  }
]
```


## Endpoints de autorização

### Criação do endpoint
Para realizar o processo de OAuth será necessário um endpoint de autorização. No Heimdall nós não temos um endpoint padrão mas provemos um interceptador para que cada Api crie o seu endpoint único. Nesse documento vamos mostrar a maneira recomendada de fazer isso, mas a customização fica a cargo do usuário.

#### 1. Resource
Na sua Api recomendamos criar um Resource separado para a as autenticações via OAuth. Sendo assim crie um novo Resource com o nome `OAuth`.

#### 2. Operation
Nesse novo Resource adicione uma Operation do tipo `POST` com o path `/oauth/token`. O resultado dessa operação dever mostrar algo assim:

![Resource](docs/assets/img/oauth/resource.png)

### Adição do Interceptor

Na aba Interceptors selecione o Resource e Operation que acabaram de ser criados e adicione o Interceptor do tipo `OAuth`. O content desse interceptor deverá conter um json como mostrado abaixo.

```json
{
  "providerId": 1,
  "typeOAuth":"AUTHORIZE",
  "privateKey": "<ClientId>",
  "timeAccessToken": 600,
  "timeRefreshToken": 1800
}
```

`providerId`: ID do provider que você criou

`typeOAuth`: para os fluxos listados nesse documento do tipo deve ser `AUTHORIZE`

`privateKey`: ClientId do App criado

`timeAccessToken`: tempo em segundos para expiração do access token

`timeRefreshToken`: tempo em segundos para expiração do refresh token

Com isso feito o seu endpoint de autenticação está criado e pronto para utilizar.

## Criação da validação

Com o endpoint de autorização criado o usuário pode escolher quais endpoints da sua Api deverão ser verificados pelo OAuth.
Para isso o usuário deverá adicionar interceptor de OAuth no Operarion, Resource ou Plan que deseja que a autenticação ocorra. No exemplo abaixo vamos adicionar a autenticação a um Resource.

Para isso na aba de Interceptors selecione o Resource que deseja proteger com o OAuth e adicione o interceptador de `OAuth` com o seguinte json no content:

```json
{
  "typeOAuth": "VALIDATE",
  "privateKey": "<ClientId>"
}
```

`typeOAuth`: o tipo `VALIDATE` indica que nesse resource será feita a validação antes de acesso ao recurso

`privateKey`: ClientId do App criado

 A partir desse ponto o setup está pronto para iniciar um dos fluxos indicados abaixo.
 
## Fluxos 
### Password Flow

Para iniciar o fluxo "password" o primeiro passo é fazer uma chamada do tipo `POST` para a url de criação de token que foi definida anteriormente. A chamada deve ser da seguinte forma:

```
POST <url_do_gateway>/api_basepath/oauth/token
Content-Type: application/json
username: <userNameValue>
password: <passwordValue>
client_id: <ClientId>

{
  "grant_type": "password"
}
```

A resposta esperada para essa chamada é um par access_token/refresh_token.
access_token: é a chave que será utilizada para consumir o recurso protegido pelo OAuth. Esse token tem o tempo de expiração dependento do que foi definido no momento da criação
refresh
```
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJncmFudF90eXBlIjoicGFzc3dvcmQiLCJpYXQiOjE1MzY2MDU2MTQsImV4cCI6MTUzNjYwNjIxNH0.BSpD-GM0nxa8ptSYXBp4jb_CgtVMtawePt7fOZ0-H1g",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJncmFudF90eXBlIjoicGFzc3dvcmQiLCJpYXQiOjE1MzY2MDU2MTQsImV4cCI6MTUzNjYwNzQxNH0.w8a2X-dd71neWfP0wrkfWSwQlj7ZyIn_Z5MNPSbBKjo",
  "token_type": "bearer",
  "expiration": 600
}

```

### Refresh Flow

O fluxo de refresh é utilizado quando um token gerado pelo `Password Flow` expira. Para utilizar basta fazer uma chamada to tipo `POST` como a mostrada abaixo.

```
POST <url_do_gateway>/api_basepath/oauth/token
client_id: <ClientId>
refresh_token: <refresh_token>

{
  "grant_type": "refresh_token"
}
```

Um novo par access_token/refresh_token será gerado sem ter que passar pelo fluxo de autenticação novamente.

### Implicit Flow

Para iniciar o fluxo implícito o primeiro passo é fazer uma chamada do tipo `POST` para a url de criação de token que foi definida anteriormente. A chamada deve ser da seguinte forma:
```
POST <url_do_gateway>/api_basepath/oauth/token
Content-Type: application/json
client_id: <client_id>

{
  "grant_type": "implicit"
}
```

`client_id`: ClientId do App criado

Caso as informações sejam validadas com sucesso será gerada uma resposta como a mostrada abaixo.

```
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJncmFudF90eXBlIjoiaW1wbGljaXQiLCJpYXQiOjE1MzQ5NjM1NTMsImV4cCI6MTUzNDk2MzU3M30.cg0MIKNwQQyBmQvAzHCsPZxiBfOR9pV07r_AxDn7Ap4",
  "expiration": 600,
  "token_type": "bearer"
}
```

Com access_token em mãos podemos começar a consumir o recurso protegido adicionando o seguinte header a requisição:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJncmFudF90eXBlIjoiaW1wbGljaXQiLCJpYXQiOjE1MzQ5NjM1NTMsImV4cCI6MTUzNDk2MzU3M30.cg0MIKNwQQyBmQvAzHCsPZxiBfOR9pV07r_AxDn7Ap4
```

Cada token só pode ser utilizado uma vez. Após consumido o token é invalidado e um novo token é adicionado ao header da resposta.

