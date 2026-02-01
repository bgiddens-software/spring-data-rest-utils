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
import org.springframework.data.util.Pair;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultParamOperationService extends HashMap<String, List<Pair<String, Operation>>>
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
				final var valueCount = values.length;
				final var ops = webRequest.getParameterMap().getOrDefault(String.format("%s%s", parameterOperationPrefix, key),
						new String[0]);
				final var pairs = new ArrayList<Pair<String, Operation>>();
				for (int i = 0; i < values.length; i++) {
					final var op = (i >= ops.length) ? new EqualTo() : switch (ops[i]) {
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
					pairs.add(Pair.of(values[i], op));
				}
				this.put(key.substring(parameterOperationPrefix.length()).toLowerCase(), pairs);
			}
		});
	}

	@Override
	public List<Pair<String, Operation>> get(String parameter) {
		return super.getOrDefault(parameter, new ArrayList<>());
	}
}
