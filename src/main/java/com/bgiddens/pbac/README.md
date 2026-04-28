# Partition-Based Access Control (PBAC)

This library contains extends Spring Security authorization features with a specific focus on
facilitating partition-based access control in conjunction with Spring Data REST.

#### What is Partition-based access control?

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
