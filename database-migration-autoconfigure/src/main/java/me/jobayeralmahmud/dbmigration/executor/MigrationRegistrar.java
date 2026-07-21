package me.jobayeralmahmud.dbmigration.executor;

import me.jobayeralmahmud.dbmigration.api.BaseMigration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Automatically executed by Spring Boot on application startup.
 * It grabs all {@link BaseMigration} beans and runs them against the
 * database using Spring JDBC templates, unless a CLI task is active.
 */
public class MigrationRegistrar implements InitializingBean {

    private final JdbcTemplate jdbcTemplate;
    private final List<BaseMigration> migrations;
    private final TransactionTemplate transactionTemplate;

    public MigrationRegistrar(
            JdbcTemplate jdbcTemplate,
            List<BaseMigration> migrations,
            TransactionTemplate transactionTemplate
    ) {
        this.migrations = migrations;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        if (migrations == null || migrations.isEmpty()) {
            System.out.println("No database migrations found to execute.");
            return;
        }
        MigrationExecutor executor = new MigrationExecutor(jdbcTemplate, transactionTemplate);
        executor.execute(migrations);
    }
}