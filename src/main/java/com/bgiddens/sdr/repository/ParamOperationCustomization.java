package com.heb.driverpay.repos;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ParamOperationCustomization
    extends HashMap<String, List<ParamOperationCustomization.OperationType>> {

  private static final String PARAM_PREFIX = "OP_";

  public enum OperationType {
    EQ,
    GT,
    LT,
    GE,
    LE,
    EQ_OR_NULL,
    GT_OR_NULL,
    LT_OR_NULL,
    GE_OR_NULL,
    LE_OR_NULL,
    LIKE,
    LIKE_IGNORE_CASE,
  }

  @Autowired private NativeWebRequest webRequest;

  @PostConstruct
  public void init() {
    webRequest
        .getParameterMap()
        .forEach(
            (key, values) -> {
              if (key.startsWith(PARAM_PREFIX) && values.length > 0) {
                this.put(
                    key.substring(PARAM_PREFIX.length()).toLowerCase(),
                    Arrays.stream(values)
                        .map(String::toUpperCase)
                        .map(OperationType::valueOf)
                        .toList());
              }
            });
  }
}
