{
  "items" : [
    {
      "title" : "Environment",
      "slug" : "environment",
      "video": "./assets/video/addEnvironment.mp4",
      "text" : "<p>The first verification from Heimdall is the environment that is linked in the request, to an Api be registered it must have a linked environment. You need to understand that the environment is the heart of the routing, Heimdall will indentify the origin route and destiny route to send. Just to be clear, one Api can be linked to more than one environment, it's your choice!</p> <p>The first step before registering an API is define some environment. To do that use the menu, under *Menu -> Environment -> Add* to start a environment register.</p>"
    },
    {
      "title" : "API",
      "slug" : "api",
      "video": "./assets/video/addApi.mp4",
      "text" : "<p>Once there is a registered Environment it is possible to register an Api, to do that use the Api menu. A Environment is required to register an Api. During the registration process of the Api its identification will be the \"basepath\". An Api is a resource group, this resources can be added after the Api registration is finished.</p>"
    },
    {
      "title" : "Resources",
      "slug" : "resources",
      "video": "./assets/video/addResource.mp4",
      "text" : "<p>The resource is nothing more than one operation group, we use the resource to organize the operations (that will be explained in the next topic). The resource registration can be initialized after an Api is added. The resource was created to organize the operations and make it easier to link the interceptors to a group.</p><p>Ex: User domain, the resource name will be \"users\"</p><p>- POST - /basePath/users<br>- GET - /basePath/users/{id}<br>- PUT - /basePath/users/{id}<br>- DELETE - /basePath/users/{id}<br></p><p>Just to be clear, a resource can have any name, but to make this example simpler we will use the names that make sense about the operations used in this documentation.</p>"
    },
    {
      "title" : "Operations",
      "slug" : "operations",
      "video": "./assets/video/addOperation.mp4",
      "text" : "<p>To complete the basic lifecycle to use the Api is necessary add some operation. The operation is a real service that the gateway will route, a group of operations make an Api. They are responsable to map the backend services that will be called. As an example we used the same paths as in the resources topic.</p><p>Ex: User domain, the resource name will be \"users\"<br>- **POST** - /basePath/**users**<br>- **GET** - /basePath/**users/{id}**<br>- **PUT** - /basePath/**users/{id}**<br>- **DELETE** - /basePath/**users/{id}**</p><p>Every line in bold above is an operation. An operation needs a http verb to be unique.</p>"
    },
    {
      "title" : "Plans",
      "slug" : "plans",
      "video": "./assets/video/addPlan.mp4",
      "text" : "<p>The plan is just a tag to a Api. It's principal function is to link some resources that Heimdall have without impact the Api directly, making possible multiple Api consumers to have their respective configurations without one affecting the other, such as two clients consuming the same Api but one want to log the requisition logs and the other does not. This way some client will have a \"Plan A\" and the other will have a \"Plan B\", where \"Plan A\" will be linked with a \"Interceptor Log\" and \"Plan B\" will not, and both will consume the same service without one impacting the other.</p>"
    },
    {
      "title" : "Interceptors",
      "slug" : "interceptors",
      "video": "./assets/video/addInterceptor.mp4",
      "text" : "<p>With Heimdall you can manipulate the requests througth the **interceptors**, you can intercept requests in lots of situations like block access to some services or just save the request logs. By default Heimdall already has some interceptors to use in an easy way.</p><p>To create an interceptor an Api needs to exist, when you select an Api you can see  a Interceptor Tab to manipulate or add a new interceptor. Using Drag N' Drop it's possible to add or remove an interceptor where you wish. Drag an interceptor to request or response area and a dialog will be shown to submit an interceptor with it's details.</p><p>**It's important to say, you can't add an interceptor directly to an Api, you can attach to a Plan, Resource or Operation that are registered to the api.**</p>"
    },
    {
      "title" : "Developers",
      "slug" : "developers",
      "video": "./assets/video/addDeveloper.mp4",
      "text" : "As we said, Heimdall is an Orchestrator, so you have lots of managers and one of their responsibilities is to manage the developers that use the Api's. A developer can use registered Apps to consume the Api's services. To manage the developers you just need to navigate to developers menu, there you can add a new developer or manipulate the registered developers."
    },
    {
      "title" : "Apps",
      "slug" : "apps",
      "video": "./assets/video/addApp.mp4",
      "text" : "The developer is able to own multiple Apps inside Heimdall, an App represents an external application that the develper wants to register in Heimdall. The App can consume any Api registered by the Heimdall. Every App created has it's own 'Client ID', this 'Client ID' in most cases is used to identify the App in the request."
    },
    {
      "title" : "Access Tokens",
      "slug" : "access-tokens",
      "video": "./assets/video/addAccessToken.mp4",
      "text" : "'Access tokens' are tokens used to provide basic access througth Heimdall. They are linked to an App, and to turn on the validation its necessary you add a Access Token Interceptor. <br><br>**It's very important you know that Access Token Interceptor require a Client ID Interceptor**"
    }
  ]
}
