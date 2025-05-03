package com.orbitflow.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final HikariDataSource dataSource ;
    private static final
    Dotenv environmentVariables = Dotenv.configure()
            .filename(".env")
            .load();

    static {
        try{
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(environmentVariables.get("STORAGE_DB_URL"));
            config.setUsername(environmentVariables.get("STORAGE_DB_USERNAME"));
            config.setPassword(environmentVariables.get("STORAGE_DB_PASSWORD"));
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
