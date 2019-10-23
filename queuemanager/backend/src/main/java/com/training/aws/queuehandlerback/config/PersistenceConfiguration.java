package com.training.aws.queuehandlerback.config;

import com.training.aws.queuehandlerback.model.DeadLetter;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        // JDBI wants to control the Connection wrap the datasource in a proxy
        // That is aware of the Spring managed transaction
        TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        Jdbi jdbi = Jdbi.create(dataSourceProxy);
        jdbi.installPlugins();

        jdbi.registerRowMapper(DeadLetter.class, ConstructorMapper.of(DeadLetter.class));

        return jdbi;
    }

}
