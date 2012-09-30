hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            pooled = true
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:postgresql://localhost:5432/locon"
            username = "locon"
            password = "locon"
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }
    test {
        dataSource {
          pooled = true
          dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
          url = "jdbc:postgresql://localhost:5432/locon"
          username = "locon"
          password = "locon"
          driverClassName = "org.postgresql.Driver"
          dialect = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }
    production {
        dataSource {
          pooled = true
          dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
          url = "jdbc:postgresql://localhost:5432/locon"
          username = "locon"
          password = "locon"
          driverClassName = "org.postgresql.Driver"
          dialect = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }
}
