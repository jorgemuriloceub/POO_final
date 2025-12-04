package br.com.biblioteca.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

/**
 * Helper para conexão e criação inicial do schema (carrega resources/sql/schema.sql).
 */
public class Database {
    private static final String URL = "jdbc:sqlite:library.db";

    static {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            // Tenta executar schema.sql se existir
            InputStream is = Database.class.getResourceAsStream("/sql/schema.sql");
            if (is != null) {
                try (Scanner s = new Scanner(is).useDelimiter("\\A")) {
                    String sql = s.hasNext() ? s.next() : "";
                    for (String stmt : sql.split(";")) {
                        String t = stmt.trim();
                        if (!t.isEmpty()) st.execute(t);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
