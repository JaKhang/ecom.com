package com.nlu.store.config;

import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.jdbc.DefaultJdbcOperations;
import com.nlu.store.core.jdbc.JdbcOperations;
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
        config.setMaximumPoolSize(propertySource.getInt("datasource.maximumPoolSize", 4)); // Thay đổi giá trị mặc định thành 4

        config.setMinimumIdle(propertySource.getInt("datasource.minimumIdle", 2)); // Số lượng kết nối nhàn rỗi tối thiểu
        config.setIdleTimeout(propertySource.getInt("datasource.idleTimeout", 30000)); // Thời gian nhàn rỗi tối đa (ms)
        config.setConnectionTimeout(propertySource.getInt("datasource.connectionTimeout", 30000)); // Thời gian chờ kết nối tối đa (ms)
        config.setMaxLifetime(propertySource.getInt("datasource.maxLifetime", 1800000)); // Thời gian sống tối đa của kết nối (ms)

        config.setConnectionTestQuery("SELECT 1"); // Câu lệnh kiểm tra kết nối
        config.setAutoCommit(true); // Tự động commit các giao dịch

        return new HikariDataSource(config);
    }

    @Produces
    public JdbcOperations createDefaultJdbcOperations() {
        return new DefaultJdbcOperations(createDataSource(), propertySource);
    }


}
