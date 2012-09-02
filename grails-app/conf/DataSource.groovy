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
            url = "jdbc:mysql://localhost:3306/market?characterEncoding=UTF-8"
            username = "root"
            password = "root"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
        }
    }
    test {
        dataSource {
          pooled = true
          dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
          url = "jdbc:mysql://localhost:3306/market?characterEncoding=UTF-8"
          username = "root"
          password = "root"
          driverClassName = "com.mysql.jdbc.Driver"
          dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
        }
    }
    production {
        dataSource {
          pooled = true
          dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
          url = "jdbc:mysql://localhost:3306/market?characterEncoding=UTF-8"
          username = "root"
          password = "root"
          driverClassName = "com.mysql.jdbc.Driver"
          dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
        }
    }
}
