package com.bgiddens.sdr.config;

import com.bgiddens.reflection.ApplicationContextHolder;
import com.bgiddens.sdr.repository.BindingsCustomizationService;
import com.bgiddens.sdr.repository.DefaultParamOperationService;
import com.bgiddens.sdr.repository.ParameterOperationService;
import com.bgiddens.sdr.repository.QueryingBindingsCustomizationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
	@ConditionalOnMissingBean
	public ParameterOperationService defaultParameterOperationService(RepositoryConfig repositoryConfig,
			NativeWebRequest webRequest) {
		return new DefaultParamOperationService(repositoryConfig.getParameterOperationPrefix(), webRequest);
	}

	@Bean
	@ConditionalOnMissingBean
	public BindingsCustomizationService queryingBindingsCustomizationService(
			ParameterOperationService defaultParameterOperationService) {
		return new QueryingBindingsCustomizationService(defaultParameterOperationService);
	}

	@Bean
	@ConditionalOnMissingBean
	public ApplicationContextHolder applicationContextHolder() {
		return new ApplicationContextHolder();
	}
}
