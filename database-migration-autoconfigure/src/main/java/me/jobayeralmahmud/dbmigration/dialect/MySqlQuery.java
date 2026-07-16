package me.jobayeralmahmud.dbmigration.dialect;

public final class MySqlQuery {

        private static final String DB_NAME = "database_migration_schemas";
        private static final String QUERY_COLUMN = "migration_tables";

        private MySqlQuery() {};

        public static String queryColumn() {
                return QUERY_COLUMN;
        }

        public static String createMigrationTable() {
                return """
                    CREATE TABLE IF NOT EXISTS %s (
                        id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        %s VARCHAR(255) NOT NULL UNIQUE,
                        executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
            """.formatted(DB_NAME, QUERY_COLUMN);
        }

        public static String findAllMigrations() {
                return "SELECT %s FROM %s ORDER BY id"
                        .formatted(QUERY_COLUMN, DB_NAME);
        }

        public static String insertMigration() {
                return "INSERT INTO %s (%s) VALUES (?)"
                        .formatted(DB_NAME, QUERY_COLUMN);
        }

        public static String deleteMigration() {
                return "DELETE FROM %s WHERE %s = ?"
                        .formatted(DB_NAME, QUERY_COLUMN);
        }
}