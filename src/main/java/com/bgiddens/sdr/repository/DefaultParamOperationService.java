package com.bgiddens.sdr.repository;

import com.bgiddens.sdr.repository.operations.EqualTo;
import com.bgiddens.sdr.repository.operations.EqualToOrNull;
import com.bgiddens.sdr.repository.operations.GreaterThan;
import com.bgiddens.sdr.repository.operations.GreaterThanOrEqualTo;
import com.bgiddens.sdr.repository.operations.GreaterThanOrEqualToOrNull;
import com.bgiddens.sdr.repository.operations.GreaterThanOrNull;
import com.bgiddens.sdr.repository.operations.LessThan;
import com.bgiddens.sdr.repository.operations.LessThanOrEqualTo;
import com.bgiddens.sdr.repository.operations.LessThanOrEqualToOrNull;
import com.bgiddens.sdr.repository.operations.LessThanOrNull;
import com.bgiddens.sdr.repository.operations.Like;
import com.bgiddens.sdr.repository.operations.LikeIgnoreCase;
import com.bgiddens.sdr.repository.operations.Operation;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultParamOperationService extends HashMap<String, List<Operation>>
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
			if (!key.startsWith(parameterOperationPrefix)) {
				final var opsParameters = webRequest.getParameterMap()
						.getOrDefault(String.format("%s%s", parameterOperationPrefix, key), new String[0]);
				final var ops = new ArrayList<Operation>();
				for (int i = 0; i < values.length; i++) {
					final var op = (i >= opsParameters.length) ? new EqualTo() : switch (opsParameters[i]) {
						case "GT" -> new GreaterThan();
						case "LT" -> new LessThan();
						case "GE" -> new GreaterThanOrEqualTo();
						case "LE" -> new LessThanOrEqualTo();
						case "EQ_OR_NULL" -> new EqualToOrNull();
						case "GT_OR_NULL" -> new GreaterThanOrNull();
						case "LT_OR_NULL" -> new LessThanOrNull();
						case "GE_OR_NULL" -> new GreaterThanOrEqualToOrNull();
						case "LE_OR_NULL" -> new LessThanOrEqualToOrNull();
						case "LIKE" -> new Like();
						case "LIKE_IGNORE_CASE" -> new LikeIgnoreCase();
						default -> new EqualTo();
					};
					ops.add(op);
				}
				this.put(key.substring(parameterOperationPrefix.length()).toLowerCase(), ops);
			}
		});
	}

	@Override
	public @NonNull Operation get(@NonNull String parameter, @NonNull Integer valueIndex) {
		if (super.containsKey(parameter) && super.get(parameter).size() > valueIndex) {
			return super.get(parameter).get(valueIndex);
		}
		return new EqualTo();
	}
}
