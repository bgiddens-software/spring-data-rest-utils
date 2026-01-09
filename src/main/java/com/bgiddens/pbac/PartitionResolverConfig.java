package com.bgiddens.pbac;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bgiddens.partitions.resolver")
public class PartitionResolverConfig {

	private Boolean forceAccess = false;

	private Boolean useInferredMethodAccessor = true;

	private Integer maxDepth = 25;

	private String packages = ".";
}
