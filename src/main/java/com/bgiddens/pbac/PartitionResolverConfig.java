package com.bgiddens.pbac;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bgiddens.partitions.resolver")
public class PartitionResolverConfig {

	private Boolean forceAccess = false;
	private Boolean useInferredMethodAccessor = true;
	private Integer maxDepth = 25;
	private String packages = ".";

	public Boolean getForceAccess() {
		return forceAccess;
	}

	public Boolean getUseInferredMethodAccessor() {
		return useInferredMethodAccessor;
	}

	public Integer getMaxDepth() {
		return maxDepth;
	}

	public String getPackages() {
		return packages;
	}

	public void setForceAccess(Boolean forceAccess) {
		this.forceAccess = forceAccess;
	}

	public void setUseInferredMethodAccessor(Boolean useInferredMethodAccessor) {
		this.useInferredMethodAccessor = useInferredMethodAccessor;
	}

	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}
}
