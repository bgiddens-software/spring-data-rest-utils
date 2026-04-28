# Dynamic Entity Graphs for REST Projections

This package contains components for dynamically creating and applying entity fetch graphs to generated JPA queries as
informed by the projection requested by the user.

## Background

### Entity Graphs

Entity graphs are a JPA feature that allows you to specify which related entities should be fetched when a query is 
executed. This can help to optimize performance by optimizing the number of queries and the number of joins needed to 
retrieve requested data.

The simplest and best known way to manipulate entity graphs is with `FetchType.EAGER` or `FetchType.LAZY` applied to 
various JPA entity relationship annotations. While this approach may be sufficient for simple tablespaces, it generates 
load strategies statically on a per-entity basis, which may not be adequate for complex systems. Often, some queries may
need to eagerly fetch certain relationships while others may not, and the performance impact of choosing appropriately 
can be significant.

To accomodate this, JPA has developed the EntityGraph process, which can be applied on a per-operation basis. 
Unfortunately, components that use this at a repository level still expect the entity graph to be static as of compile 
time, which forces the implementation in this package to be a little hackier than ideal. 

### Projections

Projections are a way to specify which fields of a data transfer objects should be returned from a REST endpoint. For a
given entity domain, an API may support a shallow projection with only the primitive fields, deeper projections with
embedded relationships, or any arbitrarily complex manipulation thereof. When using Spring Data REST, the projection is 
conventionally specified via the `projection` query parameter for the collection and item retrieval endpoints.

It quickly becomes apparent that the projection requested by the user should inform the entity graph used to fetch the
underlying data. For shallow projection requests, there is no need to join related tables, and doing so is a waste of
database resources. For deeper projection requests, tables might need to be joined eagerly to avoid an N+1 query 
problem.

## About this solution

This solution bridges the gap between projections and entity graphs as simply as possible with the following 
customizable components:
* [ProjectionContextProvider](/src/main/java/com/bgiddens/projection/ProjectionContextProvider.java) - determines how 
the currently requested projection is resolved. The default implementation looks for a named entity graph in the form of
the `projection` query parameter.
* [ProjectionEntityGraphRegistry](/src/main/java/com/bgiddens/projection/ProjectionEntityGraphRegistry.java) - a 
registry of resolved entity graphs to look up or define new entity graphs as requested. The default implementation 
initializes entity graphs lazily and stores them in memory for the duration of the application lifecycle.

Supporting these is a repository post-processor that is used by JPA to intercept and manipulate repositories as they are
created. This package adds an interceptor that reworks the standard JPA process of determining the relevant entity graph
using the previously described components.

## Usage

This implementation goes into effect by default when this library is included. To disable autoconfiguration, use the 
following property:

```yaml
bgiddens:
  sdr:
    projection-entity-graphs:
      enabled: false
```
