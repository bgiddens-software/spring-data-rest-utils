package com.bgiddens.sdr.config;

import com.bgiddens.sdr.repository.BindingCustomizationService;
import com.bgiddens.sdr.repository.ParamOperationCustomization;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.request.NativeWebRequest;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@AutoConfiguration
@EnableConfigurationProperties(RepositoryConfig.class)
@ConditionalOnProperty(value = "bgiddens.sdr.repository.enabled", matchIfMissing = true)
public class RepositoryAutoConfiguration {

	@Bean
	@Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ParamOperationCustomization paramOperationCustomization(RepositoryConfig repositoryConfig,
			NativeWebRequest webRequest) {
		return new ParamOperationCustomization(repositoryConfig.getParameterOperationPrefix(), webRequest);
	}

	@Bean
	public BindingCustomizationService bindingCustomizationService(
			ParamOperationCustomization paramOperationCustomization) {
		return new BindingCustomizationService(paramOperationCustomization);
	}
}
