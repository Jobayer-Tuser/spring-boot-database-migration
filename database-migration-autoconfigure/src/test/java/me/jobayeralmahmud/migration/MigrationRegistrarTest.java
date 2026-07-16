package me.jobayeralmahmud.migration;

import me.jobayeralmahmud.dbmigration.api.BaseMigration;
import me.jobayeralmahmud.dbmigration.schema.Schema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL"
})
@ActiveProfiles("test")
public class MigrationRegistrarTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BaseMigration testMigration1() {
            return new BaseMigration() {
                @Override
                public String migrationTablesName() {
                    return "s1_create_test_users";
                }

                @Override
                public void up(Schema schema) throws Exception {
                    schema.create("test_users", table -> {
                        table.id();
                        table.string("username", 50).unique();
                        table.timestamps();
                    });
                }

                @Override
                public void down(Schema schema) throws Exception {
                    schema.dropIfExists("test_users");
                }
            };
        }

        @Bean
        public BaseMigration testMigration2() {
            return new BaseMigration() {
                @Override
                public String migrationTablesName() {
                    return "s2_create_test_posts";
                }

                @Override
                public void up(Schema schema) throws Exception {
                    schema.create("test_posts", table -> {
                        table.id();
                        table.string("title", 100);
                        table.foreignId("user_id").referencesTable("test_users");
                        table.timestamps();
                    });
                }

                @Override
                public void down(Schema schema) throws Exception {
                    schema.dropIfExists("test_posts");
                }
            };
        }
    }

    @Test
    public void testAutomaticMigrationOnStartup() {
        // Assert that the tables created by the migrations exist
        Integer userTableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = 'TEST_USERS'", Integer.class);
        assertEquals(1, userTableCount);

        Integer postTableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = 'TEST_POSTS'", Integer.class);
        assertEquals(1, postTableCount);

        // Assert that the migration tracker table exists and contains both migrations
        List<String> migrationsList = jdbcTemplate.query(
                "SELECT migration_tables FROM database_migration_schemas ORDER BY migration_tables",
                (rs, row) -> rs.getString("migration_tables")
        );
        assertEquals(2, migrationsList.size());
        assertEquals("s1_create_test_users", migrationsList.get(0));
        assertEquals("s2_create_test_posts", migrationsList.get(1));
    }
}