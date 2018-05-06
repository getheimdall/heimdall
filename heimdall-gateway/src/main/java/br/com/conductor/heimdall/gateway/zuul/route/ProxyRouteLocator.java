
package br.com.conductor.heimdall.gateway.zuul.route;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import br.com.conductor.heimdall.gateway.zuul.storage.ZuulRouteStorage;
import lombok.extern.slf4j.Slf4j;

/**
 * <h1>ProxyRouteLocator</h1><br/>
 *
 * Represents the Heimdall's route manager.
 * 
 * @author Marcos Filho
 *
 */
@Slf4j
public class ProxyRouteLocator extends DiscoveryClientRouteLocator {

     private final ZuulRouteStorage storage;

     private DiscoveryClient discovery;

     private ZuulProperties properties;

     private AtomicReference<Map<String, ZuulRoute>> routes = new AtomicReference<>();

     public ProxyRouteLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties, ZuulRouteStorage storage) {

          super(servletPath, discovery, properties);
          this.storage = storage;
          this.discovery = discovery;
          this.properties = properties;
     }

     /**
      * Locates the routes from the {@link ZuulRouteStorage}.
      * 
      * @return A {@link LinkedHashMap} of String, {@link ZuulRoute}
      */
     @Override
     protected LinkedHashMap<String, ZuulRoute> locateRoutes() {

          log.info("Updating locate Routes");
          LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
          routesMap.putAll(init());

          if (this.discovery != null) {

               Map<String, ZuulRoute> staticServices = new LinkedHashMap<String, ZuulRoute>();
               for (ZuulRoute route : routesMap.values()) {

                    String serviceId = route.getServiceId();
                    if (serviceId == null) {
                         serviceId = route.getId();
                    }

                    if (serviceId != null) {
                         staticServices.put(serviceId, route);
                    }

               }

               // Add routes for discovery services by default
               List<String> services = this.discovery.getServices();
               String[] ignored = this.properties.getIgnoredServices().toArray(new String[0]);
               for (String serviceId : services) {

                    // Ignore specifically ignored services and those that were manually
                    // configured
                    String key = "/" + mapRouteToService(serviceId) + "/**";
                    if (staticServices.containsKey(serviceId) && staticServices.get(serviceId).getUrl() == null) {
                         // Explicitly configured with no URL, cannot be ignored
                         // all static routes are already in routesMap
                         // Update location using serviceId if location is null
                         ZuulRoute staticRoute = staticServices.get(serviceId);
                         if (!StringUtils.hasText(staticRoute.getLocation())) {
                              staticRoute.setLocation(serviceId);
                         }
                    }
                    if (!PatternMatchUtils.simpleMatch(ignored, serviceId) && !routesMap.containsKey(key)) {
                         // Not ignored
                         routesMap.put(key, new ZuulRoute(key, serviceId));
                    }

               }

          }

          if (routesMap.get(DEFAULT_ROUTE) != null) {

               ZuulRoute defaultRoute = routesMap.get(DEFAULT_ROUTE);
               // Move the defaultServiceId to the end
               routesMap.remove(DEFAULT_ROUTE);
               routesMap.put(DEFAULT_ROUTE, defaultRoute);

          }

          LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
          for (Entry<String, ZuulRoute> entry : routesMap.entrySet()) {

               String path = entry.getKey();
               // Prepend with slash if not already present.
               if (!path.startsWith("/")) {
                    path = "/" + path;
               }
               if (StringUtils.hasText(this.properties.getPrefix())) {
                    path = this.properties.getPrefix() + path;
                    if (!path.startsWith("/")) {
                         path = "/" + path;
                    }
               }
               values.put(path, entry.getValue());

          }

          this.getAtomicRoutes().set(values);
          return values;

     }

     private LinkedHashMap<String, ZuulRoute> init() {

          log.info("Calling Storage.findAll()");
          LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
          for (ZuulRoute route : storage.findAll()) {
               routesMap.put(route.getPath(), route);
          }
          return routesMap;

     }

     /**
      * Returns the routes
      * @return	{@link AtomicReference} of String, {@link ZuulRoute}
      */
     public AtomicReference<Map<String, ZuulRoute>> getAtomicRoutes() {

          return routes;
     }
}
