hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            pooled = true
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:postgresql://localhost:5432/soisbelle"
            /*url = "jdbc:postgresql://postgres-soisbelle.j.rsnx.ru:5432/soisbelle"*/
            username = "soisbelle"
            password = "Password1"
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }
    test {
        dataSource {
          pooled = true
          dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
          url = "jdbc:postgresql://localhost:5432/soisbelle"
          /*url = "jdbc:postgresql://postgres-soisbelle.j.rsnx.ru:5432/soisbelle"*/
          username = "soisbelle"
          password = "Password1"
          driverClassName = "org.postgresql.Driver"
          dialect = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }
    production {
        dataSource {
          pooled = true
          dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
          url = "jdbc:postgresql://localhost:5432/soisbelle"
          /*url = "jdbc:postgresql://postgres-soisbelle.j.rsnx.ru:5432/soisbelle"*/
          username = "soisbelle"
          password = "Password1"
          driverClassName = "org.postgresql.Driver"
          dialect = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }
}
