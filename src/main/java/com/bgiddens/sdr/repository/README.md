# Queryable Repositories

The `QueryingRepository` and `QueryingViewRepository` interfaces extend `JpaRepository` for use with Spring Data REST's `@RepositoryRestResource` annotation.

These classes integrate with Querydsl to allow for powerful filtering of the controller's collection endpoint.

For example, to fetch entities where `myField` is between 10 inclusive and 20 exclusive, a request can be made as follows:

```
GET /myEntities?myField=10&myField=20&OP_myField=ge&OP_myField=lt
```

Available operations are `EQ`, `GT`, `LT`, `GE`, `LE`, `EQ_OR_NULL`, `GT_OR_NULL`, `LT_OR_NULL`, `GE_OR_NULL`, `LE_OR_NULL`, `LIKE`, and `LIKE_IGNORE_CASE`.

This feature is experimental and should be updated for further extensibility in a future version.
