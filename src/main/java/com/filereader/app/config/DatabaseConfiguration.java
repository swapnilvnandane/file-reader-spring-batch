package com.filereader.app.config;

import com.filereader.app.config.props.DatabaseProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "fileReaderEM", transactionManagerRef = "fileReaderTM", basePackages = {"com.filereader.app.repository"})
public class DatabaseConfiguration {

    private static final String ENTITY_PKG = "com.filereader.app.entity";
    private static final String PERSISTENCE_UNIT = "file-reader-batch";
    private static final String POOL_NAME = PERSISTENCE_UNIT;

    private final DatabaseProperties databaseProperties;

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource source = DataSourceBuilder.create()
                .driverClassName(databaseProperties.getDriverClassName())
                .url(databaseProperties.getConnectionUrl())
                .username(databaseProperties.getUsername())
                .password(databaseProperties.getPassword())
                .type(HikariDataSource.class)
                .build();
        HikariConfig config = new HikariConfig();
        config.setDataSource(source);
        config.setConnectionTestQuery(databaseProperties.getValidationQuery());
        config.setMinimumIdle(databaseProperties.getMinIdle());
        config.setPoolName(POOL_NAME);
        config.setMaximumPoolSize(databaseProperties.getMaxActive());
        config.setConnectionTimeout(databaseProperties.getMaxWait());
        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean fileReaderEM(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource()).packages(ENTITY_PKG).persistenceUnit(PERSISTENCE_UNIT).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager fileReaderTM(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(fileReaderEM(builder).getObject());
        return transactionManager;
    }
}