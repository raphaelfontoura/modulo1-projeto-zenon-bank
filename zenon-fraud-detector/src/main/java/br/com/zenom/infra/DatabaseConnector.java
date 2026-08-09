package br.com.zenom.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private DatabaseConnector() {}

    public static Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/zenomdb?rewriteBatchedStatements=true", "root", "senha123");
    }
}
