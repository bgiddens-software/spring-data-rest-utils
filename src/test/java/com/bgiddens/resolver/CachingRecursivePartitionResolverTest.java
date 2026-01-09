package com.bgiddens.resolver;

import com.bgiddens.pbac.Partitionable;
import com.bgiddens.pbac.PartitionResolverConfig;
import com.bgiddens.pbac.resolver.CachingRecursivePartitionResolver;
import com.bgiddens.pbac.resolver.DefaultPartitionableClassScanner;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class CachingRecursivePartitionResolverTest {

	@AllArgsConstructor
	protected static class PartitionBasisA {
		@Partitionable(basis = "A") private String foo;
	}

	@AllArgsConstructor
	protected static class PartitionBasisB {

		private String foo;

		@Partitionable(basis = "B")
		public String getBar() {
			return foo;
		}
	}

	@AllArgsConstructor
	protected static class PartitionableA {
		@Partitionable(basis = "A") private PartitionBasisA partition;
	}

	@AllArgsConstructor
	protected static class PartitionableB {
		@Partitionable(basis = "A") private List<PartitionBasisA> partitionsA;

		@Partitionable(basis = "B") private PartitionBasisB partitionB;
	}

	@AllArgsConstructor
	protected static class PartitionableC {
		@Partitionable(basis = "A") private List<PartitionableA> partitionsA;

		@Partitionable(basis = { "A", "B" }) private List<PartitionableB> partitionsB;
	}

	@Test
	public void test() {
		var scanner = new DefaultPartitionableClassScanner("com.bgiddens.resolver");
		var config = new PartitionResolverConfig();

		config.setForceAccess(true);
		config.setUseInferredMethodAccessor(false);
		var resolver = new CachingRecursivePartitionResolver(scanner, config);
		resolver.cachePartitionableObjects();

		var partitionableA = new PartitionableA(new PartitionBasisA("A1"));
		var partitionableB = new PartitionableB(List.of(new PartitionBasisA("A2"), new PartitionBasisA("A3")),
				new PartitionBasisB("B1"));
		var partitionableC = new PartitionableC(
				List.of(new PartitionableA(new PartitionBasisA("A4")), new PartitionableA(new PartitionBasisA("A1"))),
				List.of(
						new PartitionableB(List.of(new PartitionBasisA("A5"), new PartitionBasisA("A6")),
								new PartitionBasisB("B1")),
						new PartitionableB(List.of(new PartitionBasisA("A3"), new PartitionBasisA("A5")),
								new PartitionBasisB("B3"))));

		var partitionsAA = resolver.resolvePartitions("A", partitionableA);
		assertThat(partitionsAA.size(), equalTo(1));
		assertThat(partitionsAA, containsInAnyOrder("A1"));

		var partitionsBA = resolver.resolvePartitions("A", partitionableB);
		assertThat(partitionsBA.size(), equalTo(2));
		assertThat(partitionsBA, containsInAnyOrder("A2", "A3"));

		var partitionsBB = resolver.resolvePartitions("B", partitionableB);
		assertThat(partitionsBB.size(), equalTo(1));
		assertThat(partitionsBB, containsInAnyOrder("B1"));

		var partitionsCA = resolver.resolvePartitions("A", partitionableC);
		assertThat(partitionsCA.size(), equalTo(5));
		assertThat(partitionsCA, containsInAnyOrder("A1", "A3", "A4", "A5", "A6"));

		var partitionsCB = resolver.resolvePartitions("B", partitionableC);
		assertThat(partitionsCB.size(), equalTo(2));
		assertThat(partitionsCB, containsInAnyOrder("B1", "B3"));
	}
}
