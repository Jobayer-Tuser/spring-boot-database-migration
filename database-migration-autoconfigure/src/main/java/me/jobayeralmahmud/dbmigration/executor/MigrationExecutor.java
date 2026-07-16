package me.jobayeralmahmud.dbmigration.executor;

import me.jobayeralmahmud.dbmigration.api.BaseMigration;
import me.jobayeralmahmud.dbmigration.dialect.MySqlQuery;
import me.jobayeralmahmud.dbmigration.schema.Schema;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A database migration runner utilizing Spring JDBC.
 * Automatically tracks executed migrations and rolls back failed ones.
 */
public class MigrationExecutor {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public MigrationExecutor(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Runs pending migrations in sequential order, skipping already-executed ones.
     */
    public void run(List<BaseMigration> migrations) {
        createTablesIfNotExists();
        List<String> executed = collectSuccessfulMigrationTables();

        migrations.stream()
                .sorted(Comparator.comparing(BaseMigration::migrationTablesName))
                .filter(table -> !executed.contains(table.migrationTablesName()))
                .forEach(table -> {
                    info("Running migration: " + table.migrationTablesName());
                    runMigration(table);
                });
    }


    public void rollback(List<BaseMigration> migrations, int steps) {
        createTablesIfNotExists();

        List<String> executed = collectSuccessfulMigrationTables();
        if (executed.isEmpty()) {
            info("No migrations to rollback.");
            return;
        }

        List<String> toRollback = new ArrayList<>(
                executed.subList(Math.max(0, executed.size() - steps), executed.size()));
        Collections.reverse(toRollback);

        for (String name : toRollback) {
            BaseMigration migration = migrations.stream()
                    .filter(m -> m.migrationTablesName().equals(name))
                    .findFirst()
                    .orElse(null);

            if (migration == null) {
                error("Cannot rollback " + name + ": migration class not found.");
                continue;
            }

            info("Rolling back migration: " + name);
            try {
                transactionTemplate.executeWithoutResult(status -> {
                    try {
                        migration.down(new Schema(jdbcTemplate));
                        deleteMigratedTables(name);
                        info("Rollback of " + name + " successful.");
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        throw new RuntimeException("Rollback failed: " + name, e);
                    }
                });
            } catch (Exception e) {
                error(e.getMessage());
            }
        }
    }


    private void runMigration(BaseMigration migration) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                migration.up(new Schema(jdbcTemplate));
                storeMigratedTables(migration.migrationTablesName());
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException("Migration failed: " + migration.migrationTablesName(), e);
            }
        });
    }

    private List<String> collectSuccessfulMigrationTables() {
        return jdbcTemplate.query(MySqlQuery.findAllMigrations(),
                (rs, rowNum) -> rs.getString(MySqlQuery.queryColumn()));
    }

    private void createTablesIfNotExists() {
        jdbcTemplate.execute(MySqlQuery.createMigrationTable());
    }

    private void storeMigratedTables(String name) {
        jdbcTemplate.update(MySqlQuery.insertMigration(), name);
    }

    private void deleteMigratedTables(String name) {
        jdbcTemplate.update(MySqlQuery.deleteMigration(), name);
    }

    private static void info(String message) {
        System.out.println(message);
    }
    private static void error(String message) {
        System.err.println(message);
    }
}