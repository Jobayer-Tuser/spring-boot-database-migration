package me.jobayeralmahmud.autoconfigure;

import me.jobayeralmahmud.migration.BaseMigration;
import me.jobayeralmahmud.migration.MigrationInitializer;
import me.jobayeralmahmud.properties.DatabaseConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(DatabaseConfigurationProperties.class)
public final class SpringBootDatabaseMigrationAutoConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public MigrationInitializer migrationInitializer(
            DataSource dataSource,
            PlatformTransactionManager transactionManager,
            ObjectProvider<List<BaseMigration>> migrationsProvider) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        List<BaseMigration> migrations = migrationsProvider.getIfAvailable(ArrayList::new);
        return new MigrationInitializer(jdbcTemplate, transactionTemplate, migrations);
    }
}