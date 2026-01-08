package com.bgiddens.sdr.repository;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;

public class ParamOperationCustomization extends HashMap<String, List<ParamOperationCustomization.OperationType>> {

	public ParamOperationCustomization(String parameterOperationPrefix, NativeWebRequest webRequest) {
		this.parameterOperationPrefix = parameterOperationPrefix;
		this.webRequest = webRequest;
	}

	private final String parameterOperationPrefix;
	private final NativeWebRequest webRequest;

	public enum OperationType {
		EQ, GT, LT, GE, LE, EQ_OR_NULL, GT_OR_NULL, LT_OR_NULL, GE_OR_NULL, LE_OR_NULL, LIKE, LIKE_IGNORE_CASE,
	}

	@PostConstruct
	public void init() {
		webRequest.getParameterMap().forEach((key, values) -> {
			if (key.startsWith(parameterOperationPrefix) && values.length > 0) {
				this.put(key.substring(parameterOperationPrefix.length()).toLowerCase(),
						Arrays.stream(values).map(String::toUpperCase).map(OperationType::valueOf).toList());
			}
		});
	}
}
