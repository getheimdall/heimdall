# Heimdall Changelog

### v1.7.0-stable
* **BreakChange**
   * Log interceptor as ZuulFilter (now log are embedded and will be registered in all request's, and interceptors logs will be deleted) [Pull Request #130](https://github.com/getheimdall/heimdall/pull/130);

* **Feature**

   * Adding id's on frontend component's to improve tests [Pull Request #127](https://github.com/getheimdall/heimdall/pull/127);
   * Adding metrics resource, All metrics will be extracted from the logs that Heimdall saves on mongoDB. [Pull Request #128](https://github.com/getheimdall/heimdall/pull/128);
   * Optional disable 4xx error handler in http client on Heimdall-spec [Pull Request #129](https://github.com/getheimdall/heimdall/pull/129);
   * Adding stack trace per filter [Pull Request #131](https://github.com/getheimdall/heimdall/pull/131);
   * Improve the interceptor's UX on frontend [Pull Request #132](https://github.com/getheimdall/heimdall/pull/132);
   * Adding HTTP PATCH method support to spec [Pull Request #135](https://github.com/getheimdall/heimdall/pull/135);
   * Improve algorithm to cache interceptor delete with more precision [Pull Request #136](https://github.com/getheimdall/heimdall/pull/136);
   * Added Api level to Interceptors [Pull Request #136](https://github.com/getheimdall/heimdall/pull/137);

* **Bugfix**

   * Fixed bug dropdown menus fixed when scrolling page [Pull Request #133](https://github.com/getheimdall/heimdall/pull/133);
   * Middleware file response not working [Pull Request #123](https://github.com/getheimdall/heimdall/pull/123);

### v1.6.6-stable 
* **Feature**

   * Adding bean validation do middleware [Pull Request #115](https://github.com/getheimdall/heimdall/pull/115);
   * The error request will not be dispatched to Spring error controller, the response will write in the same request [Pull Request #126](https://github.com/getheimdall/heimdall/pull/126);

* **Bugfix**

   * Middleware file response not working [Pull Request #123](https://github.com/getheimdall/heimdall/pull/123);

### v1.6.5-stable 
* **Bugfix**

   * Fixing the middleware registration when a new middleware are added to a new api [Pull Request #120](https://github.com/getheimdall/heimdall/pull/120)
   * Updating changelogs

### v1.6.3-stable 
* **Bugfix**

    * Changing the mongo appender to async, sync appender generating problems when the mongo off [Pull Request #117](https://github.com/getheimdall/heimdall/pull/117)
    * Removing cloud config health check 

### v1.6.1-stable
* **Bugfix**
	
    * Rollback the spring cloud dependency to tracking some possible thread leak from middlewares;

### v1.6.0-stable

* **Feature**

    * Adding cache interceptor [Pull Request #109](https://github.com/getheimdall/heimdall/pull/109);
    
* **Bugfix**	

    * Fix middleware listing when using paging [Pull Request #108](https://github.com/getheimdall/heimdall/pull/108); 
    * Fix rate limit interceptor, wasn't working correctly [Pull Request #111](https://github.com/getheimdall/heimdall/pull/111);
    * Fix client id interceptor validation [Pull Request #110](https://github.com/getheimdall/heimdall/pull/110); 

### v1.5.0-stable

* **Feature**

    * Adding feature to download Middleware file [Pull Request #105](https://github.com/getheimdall/heimdall/pull/105)
    
* **Bugfix**

    * The class path loader when adding a new middleware had some issues that in some circumstances could cause the new middleware to not be loaded. [Pull Request #96](https://github.com/getheimdall/heimdall/pull/96)

### v1.4.0-stable

* **Feature**

    * Updates in front-end [Pull Request #74](https://github.com/getheimdall/heimdall/pull/74)
        * Dispatch to update list the middlewares when save one middleware;
        * Add method ALL in operations;
        * Create template to Blacklist and Whitelist;
        *  Filter traces not null to show in Traces;
    * Adding implicit OAuth [Pull Request #88](https://github.com/getheimdall/heimdall/pull/88)
        * Refactored code
        * Change methods to static in JwtUtils.java
    * Changed login page layout [Pull Request #90](https://github.com/getheimdall/heimdall/pull/90)
    * Adding scroll in tables and update list of the environments [Pull Request #92](https://github.com/getheimdall/heimdall/pull/92)
    * Allow creation of one app with informed clientId [Pull Request #89](https://github.com/getheimdall/heimdall/pull/89)
        * Create new class AppPersist
        * Create tests
    
* **Bugfix**

    * Remove header method from middleware helper class was not working. [Pull Request #76](https://github.com/getheimdall/heimdall/pull/76)

### v1.3.0-stable

* **Feature**

    * Initial changelog.
    * Upload middleware file by front-end. [Pull Request #73](https://github.com/getheimdall/heimdall/pull/73)
    * Screen traces with filters. [Pull Request #71](https://github.com/getheimdall/heimdall/pull/71)
    * Fix inline javascript  bug. [Pull Request #70](https://github.com/getheimdall/heimdall/pull/70)
    * Blacklist and Whitelist interceptors. [Pull Request #69](https://github.com/getheimdall/heimdall/pull/69)
    * Upgrading spring parent version. [Pull Request #68](https://github.com/getheimdall/heimdall/pull/68)
    * Adjusted trace resource to performe searches based on a list of filters. [Pull Request #67](https://github.com/getheimdall/heimdall/pull/67)
    * Show list of accessTokens from an App. [Pull Request #63](https://github.com/getheimdall/heimdall/pull/63)
    * Trace Resource. [Pull Request #62](https://github.com/getheimdall/heimdall/pull/62)
    * Add objects in payload of the token. [Pull Request #61](https://github.com/getheimdall/heimdall/pull/61)
    * Add http method ALL. [Pull Request #59](https://github.com/getheimdall/heimdall/pull/59)
    
* **Bugfix**

    * Fixed misspelled "groovy" in log for listener. [Pull Request #65](https://github.com/getheimdall/heimdall/pull/65)
    * Url pattern not working properly. [Pull Request #64](https://github.com/getheimdall/heimdall/pull/64)
    * Fix cors duplicated. [Pull Request #60](https://github.com/getheimdall/heimdall/pull/60)
    * Date serialization. [Pull Request #66](https://github.com/getheimdall/heimdall/pull/66)
    * Fix validation problems when somebody update an operation  bug. [Pull Request #72](https://github.com/getheimdall/heimdall/pull/72)

