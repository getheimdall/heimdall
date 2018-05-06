
package br.com.conductor.heimdall.api.configuration;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import br.com.conductor.heimdall.api.environment.LdapProperty;

/**
 * <h1>SecurityConfiguration</h1><br/>
 * 
 * Extends the {@link WebSecurityConfigurerAdapter} class.
 *
 * @author Marcos Filho
 *
 */
@Configuration
@ConditionalOnProperty("heimdall.security.enabled")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
     
     @Autowired
     private LdapProperty ldapProps;
     
     @Autowired
     private UserDetailsService userDetailsService;
     
     @Autowired
     private LdapAuthoritiesPopulator populator;
     
     @Autowired
     private PasswordEncoder passwordEncoder;

     @Override
     protected void configure(HttpSecurity httpSecurity) throws Exception {

          httpSecurity
               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               .and()
               .authorizeRequests().antMatchers("/error").permitAll()
               .and()
               .authorizeRequests().antMatchers("/v1/api/integrations/**").permitAll()
               .anyRequest().authenticated()
               .and()
               .httpBasic()
               .and()
               .logout().permitAll();

          httpSecurity.csrf().disable();
          httpSecurity.headers().frameOptions().disable();

     }

     @Override
     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
          
          if (ldapProps.isEnabled()) {
               
               auth.authenticationProvider(ldapProvider());
          }
          auth.authenticationProvider(jdbcProvider());
     }
     
     /**
      * Returns a configured {@link DaoAuthenticationProvider}.
      * 
      * @return {@link DaoAuthenticationProvider}
      */
     @Bean
     public DaoAuthenticationProvider jdbcProvider() {
         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
         authProvider.setUserDetailsService(userDetailsService);
         authProvider.setPasswordEncoder(passwordEncoder);
         return authProvider;
     }
     
     /**
      * Returns a configured {@link LdapAuthenticationProvider}.
      * 
      * @return {@link LdapAuthenticationProvider}
      */
     @Bean
     @ConditionalOnProperty("heimdall.security.ldap.enabled")
     public LdapAuthenticationProvider ldapProvider() {
          
          LdapContextSource contextSource = new LdapContextSource();
          contextSource.setUrl(ldapProps.getUrl());
          contextSource.setUserDn(ldapProps.getUserDn());
          contextSource.setPassword(ldapProps.getPassword());
          contextSource.setReferral("follow");
          contextSource.afterPropertiesSet();
          
          LdapUserSearch ldapUserSearch = new FilterBasedLdapUserSearch(ldapProps.getSearchBase(), ldapProps.getUserSearchFilter(), contextSource);
          
          BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
          bindAuthenticator.setUserSearch(ldapUserSearch);
          return new LdapAuthenticationProvider(bindAuthenticator, populator);
     }
}
