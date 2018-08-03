# Heimdall Changelog

### v1.4.1
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

