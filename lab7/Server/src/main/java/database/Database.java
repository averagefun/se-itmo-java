package database;

import org.intellij.lang.annotations.Language;
import java.sql.*;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private final Logger log = LoggerFactory.getLogger(Database.class);
    Connection connection;
    PreparedStatement stmt;

    public Database(String user, String password) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:postgresql://localhost:5432/postgres";
        connection = DriverManager.getConnection(url, user, password);
    }

    private PreparedStatement parseSql(String sql, Object[] args) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 1; i<=args.length; i++) {
            Object arg = args[i-1];
            if (arg == null) {
                stmt.setNull(i, Types.OTHER);
            } else if (arg instanceof String) {
                stmt.setString(i, arg.toString());
            } else if (arg instanceof Enum) {
                stmt.setObject(i, ((Enum<?>) arg).name(), Types.OTHER);
            } else if (arg instanceof Integer) {
                stmt.setInt(i, (Integer)arg);
            } else if (arg instanceof Long) {
                stmt.setLong(i, (Long)arg);
            } else if (arg instanceof Double) {
                stmt.setDouble(i, (Double)arg);
            } else if (arg instanceof Float) {
                stmt.setFloat(i, (Float)arg);
            } else if (arg instanceof LocalDate) {
                stmt.setObject(i, arg);
            } else {
                stmt.close();
                throw new SQLException("unknown data type");
            }
        }
        return stmt;
    }

    public int executeUpdate(@Language("SQL")String sql, Object... args) throws SQLException {
        stmt = parseSql(sql, args);
        int answer = stmt.executeUpdate();
        stmt.close();
        return answer;
    }

    public ResultSet executeQuery(@Language("SQL")String sql, Object... args) throws SQLException {
        stmt = parseSql(sql, args);
        return stmt.executeQuery();
    }

    public void closeStmt() throws SQLException {
        stmt.close();
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
