package me.jobayeralmahmud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "migration.database.driver")
public class DatabaseConfigurationProperties {
}