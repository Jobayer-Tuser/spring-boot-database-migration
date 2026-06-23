package me.jobayeralmahmud.migration;

final class MigrationQueries {
        private MigrationQueries() {
        } // Prevent instantiation

        static final String CREATE_TABLE = """
                        CREATE TABLE IF NOT EXISTS database_migration_schema (
                            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                            migration_tables VARCHAR(255) NOT NULL UNIQUE,
                            executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )""";

        static final String SELECT_MIGRATIONS = "SELECT migration_tables FROM database_migration_schema ORDER BY id ASC";

        static final String INSERT_MIGRATION = "INSERT INTO schema_migrations (migration_tables) VALUES (?)";

        static final String DELETE_MIGRATION = "DELETE FROM schema_migrations WHERE migration_tables = ?";
}