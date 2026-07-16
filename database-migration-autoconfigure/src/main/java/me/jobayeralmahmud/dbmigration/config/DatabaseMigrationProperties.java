package me.jobayeralmahmud.dbmigration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "migration.database")
public class DatabaseMigrationProperties {
}