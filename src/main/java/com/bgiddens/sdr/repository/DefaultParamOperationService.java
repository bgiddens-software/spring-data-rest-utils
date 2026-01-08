package com.bgiddens.sdr.repository;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;

public class DefaultParamOperationService extends HashMap<String, List<OperationType>>
		implements ParameterOperationService {

	public DefaultParamOperationService(String parameterOperationPrefix, NativeWebRequest webRequest) {
		this.parameterOperationPrefix = parameterOperationPrefix;
		this.webRequest = webRequest;
	}

	private final String parameterOperationPrefix;
	private final NativeWebRequest webRequest;

	@PostConstruct
	public void init() {
		webRequest.getParameterMap().forEach((key, values) -> {
			if (key.startsWith(parameterOperationPrefix) && values.length > 0) {
				this.put(key.substring(parameterOperationPrefix.length()).toLowerCase(),
						Arrays.stream(values).map(String::toUpperCase).map(OperationType::valueOf).toList());
			}
		});
	}

	@Override
	public List<OperationType> get(String parameter) {
		return super.getOrDefault(parameter, new ArrayList<>());
	}
}
