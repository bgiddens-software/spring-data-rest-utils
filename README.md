# Utilities for Spring Data REST


### Partition-Based Access Control

This library contains extends Spring Security authorization features with a specific focus on
facilitating partition-based access control in conjunction with Spring Data REST.

#### What is Partition-based access control (PBAC)?

Partition-based access control restricts an action based on the subset of data that it affects.
This PBAC implementation is interoperable with operation-based access control (RBAC), which applies access restrictions at the operation level.

For example, a conventional operation-based access control scheme may allow read operations on a dataset to be accessed by all users,
while write operations are accessible only to administrators. A PBAC layer could extend this by allowing all users access to
write operations, but only on data that they own.

> Note: the term "partition" is used conceptually in this context and is not intended to be semantically
consistent with other definitions, such as that of the SQL standard.

#### Features

This library implements request screening as well as automatic collection filtering for `QuerydslPredicateExecutor`s.
Requests to save, retrieve, or delete an item will be screened and will return a 403 if partition authorization fails.
If a REST Repository does not implement `QuerydslPredicateExecutor`, requests to retrieve collection data will return 403 for users with partitioned access.
If a REST Repository implements `QuerydslPredicateExecutor`, then requests to the retrieve collections will automatically filter based on the user's permitted partitions.

#### Getting Started

> Tip: the `src/test` directory of this repository contains a fully functioning demonstration that can be used as a starting point.

To use this implementation, define beans implementing the following interfaces:
* `AccessRegistry` - this is where you will assign levels of access based on the user's authentication and the operation domain type.
* `AuthenticationPartitionResolver` - this component defines how partitions can be found for a user's `Authentication`.
  For example, they may be defined as `GrantedAuthority`s or they may be resolvable from an attached database, depending on the security implementation.

Next, set the packages to scan for `@Partitionable` annotations using a configuration property like the following.

```yaml
bgiddens:
  partitions:
    resolver:
      packages: com.bgiddens.impl.entities
```

Next, make an entity partitionable by annotating the field that should be used as the basis for partitioning with the
`@Partitionable` annotation. If the implementation will need to partition entities on multiple different bases, then a basis should be specified.
Otherwise, the parameter can be omitted (which uses a default basis of \<empty string\>).

Finally, just use Spring Data REST as you otherwise would, and this library will filter requests by matching
the affected data's partitions to the user's partitions.

Partitions can be resolved across an arbitrary number of associations within the entity network.

## Queryable Repositories

The `QueryingRepository` and `QueryingViewRepository` interfaces extend `JpaRepository` for use with Spring Data REST's `@RepositoryRestResource` annotation. 

These classes integrate with Querydsl to allow for powerful filtering of the controller's collection endpoint.

For example, to fetch entities where `myField` is between 10 inclusive and 20 exclusive, a request can be made as follows:

```
GET /myEntities?myField=10&myField=20&OP_myField=ge&OP_myField=lt
```

Available operations are `EQ`, `GT`, `LT`, `GE`, `LE`, `EQ_OR_NULL`, `GT_OR_NULL`, `LT_OR_NULL`, `GE_OR_NULL`, `LE_OR_NULL`, `LIKE`, and `LIKE_IGNORE_CASE`.

This feature is experimental and should be updated for further extensibility in a future version.

## Contributing

If you identify an issue or have a feature request, please open an issue.

Where possible, development practices in this repository are modeled on those of the [Spring Data REST](https://github.com/spring-projects/spring-data-rest) project.
