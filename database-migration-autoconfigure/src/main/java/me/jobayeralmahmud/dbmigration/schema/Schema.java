package me.jobayeralmahmud.dbmigration.schema;

import org.springframework.jdbc.core.JdbcTemplate;
import java.util.function.Consumer;

public class Schema {

    private final JdbcTemplate jdbcTemplate;

    public Schema(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(String tableName, Consumer<Blueprint> callback) {
        Blueprint table = new Blueprint(tableName);
        callback.accept(table);
        execute(table.getSql(tableName));
    }

    public void dropIfExists(String tableName) {
        execute("DROP TABLE IF EXISTS " + tableName);
    }

    public void table(String tableName, Consumer<Blueprint> callback) {
        Blueprint table = new Blueprint(tableName);
        callback.accept(table);

        for (String sql : table.getAlterationSql(tableName)) {
            execute(sql);
        }
    }

    private void execute(String sql) {
        jdbcTemplate.execute(sql);
    }
}