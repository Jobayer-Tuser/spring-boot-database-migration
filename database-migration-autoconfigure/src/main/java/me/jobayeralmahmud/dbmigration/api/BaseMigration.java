package me.jobayeralmahmud.dbmigration.api;

import me.jobayeralmahmud.dbmigration.schema.Schema;

public abstract class BaseMigration {

    /**
     * Forward migration logic (create tables, insert data, etc.).
     */
    public abstract void up(Schema schema) throws Exception;

    /**
     * Rollback migration logic (drop tables, remove data, etc.).
     */
    public void down(Schema schema) throws Exception {}

    /**
     * Helper to easily output logs.
     */
    protected void log(String message) {
        System.out.println("✓ " + message);
    }
    
    /**
     * Automatically extracts the version from the class name
     * example(s1_create_users_table).
     */
    public String migrationTablesName() {
        var className = this.getClass().getSimpleName();
        return className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}