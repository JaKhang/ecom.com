package com.nlu.store.config;

import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.dao.DefaultJdbcOperations;
import com.nlu.store.core.dao.JdbcOperations;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import javax.sql.DataSource;

@ApplicationScoped
public class JdbcOperationsProducer {

    private final PropertySource propertySource;

    @Inject
    public JdbcOperationsProducer(PropertySource propertySource) {
        this.propertySource = propertySource;
    }


    private DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(propertySource.getString("datasource.url"));
        config.setUsername(propertySource.getString("datasource.username"));
        config.setPassword(propertySource.getString("datasource.password"));
        config.setDriverClassName(propertySource.getString("datasource.driverClassName"));
        config.setMaximumPoolSize(propertySource.getInt("datasource.maximumPoolSize", 10));
        return new HikariDataSource(config);
    }

    @Produces
    public JdbcOperations createDefaultJdbcOperations() {
        return new DefaultJdbcOperations(createDataSource());
    }


}
