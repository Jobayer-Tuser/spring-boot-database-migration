package me.jobayeralmahmud.dbmigration.autoconfigure;

import me.jobayeralmahmud.dbmigration.api.BaseMigration;
import me.jobayeralmahmud.dbmigration.executor.MigrationRegistrar;
import me.jobayeralmahmud.dbmigration.config.DatabaseMigrationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@ConditionalOnBean(JdbcTemplate.class)
@ConditionalOnClass(JdbcTemplate.class)
@EnableConfigurationProperties(DatabaseMigrationProperties.class)
public final class DatabaseMigrationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MigrationRegistrar migrationInitializer(
            DataSource dataSource,
            PlatformTransactionManager transactionManager,
            ObjectProvider<List<BaseMigration>> migrationsProvider
    ) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<BaseMigration> migrations = migrationsProvider.getIfAvailable(ArrayList::new);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return new MigrationRegistrar(jdbcTemplate, migrations, transactionTemplate);
    }
}