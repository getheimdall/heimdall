package br.com.conductor.heimdall.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.conductor.heimdall.gateway.filter.helper.CallImpl;
import br.com.conductor.heimdall.gateway.filter.helper.HelperImpl;
import br.com.conductor.heimdall.middleware.spec.Call;
import br.com.conductor.heimdall.middleware.spec.Helper;

/**
 * 
 * @author marcos.filho
 *
 */
@Configuration
public class HelperConfiguration {

	@Bean
	public Helper helper() {
		return new HelperImpl();
	}
	
	@Bean
	public Call call() {
		return new CallImpl();
	}
	
}
