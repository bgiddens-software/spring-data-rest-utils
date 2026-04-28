# Utilities for Spring Data REST

## Partition-Based Access Control

Components for controlling user access by data partition in parallel with role-based access control. Separate from and
compatible with multitenancy, PBAC allows for sharing of partitions and dynamic access to multiple partitions of
multiple bases. Full documentation [here](src/main/java/com/bgiddens/pbac/README.md).

## Queryable Repositories

Extensions of Spring Data REST's repository pattern to empower CRUD controllers with parametric operational control 
based on the fields of the underlying entities. Note that this can be extremely powerful and may require guardrails to 
avoid giving users too much power to create expensive queries. Full documentation 
[here](src/main/java/com/bgiddens/sdr/repository/README.md).

## Projection-driven Entity Graphs

Automatic augmentation of JPA repositories to dynamically apply entity graphs based on the projections requested by the 
user. Full documentation [here](src/main/java/com/bgiddens/projection/README.md).

## Contributing

If you identify an issue or have a feature request, please open an issue.

Where possible, development practices in this repository are modeled on those of the 
[Spring Data REST](https://github.com/spring-projects/spring-data-rest) project.
