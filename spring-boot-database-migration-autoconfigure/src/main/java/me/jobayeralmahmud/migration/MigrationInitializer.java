package me.jobayeralmahmud.migration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Automatically executed by Spring Boot on application startup.
 * It grabs all {@link BaseMigration} beans and runs them against the
 * database using Spring JDBC templates.
 */
public class MigrationInitializer implements InitializingBean {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final List<BaseMigration> migrations;

    public MigrationInitializer(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, List<BaseMigration> migrations) {
        this.migrations = migrations;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (migrations == null || migrations.isEmpty()) {
            System.out.println("No database migrations found to execute.");
            return;
        }

        MigrationRunner runner = new MigrationRunner(jdbcTemplate, transactionTemplate);
        runner.run(migrations);
    }
}