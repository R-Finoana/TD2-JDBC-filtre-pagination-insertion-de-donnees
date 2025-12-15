package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    String jdbcUrl = System.getenv("jdbcUrl");
    String username = System.getenv("username");
    String password = System.getenv("password");

    public Connection getDBConnection(){
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e){
            throw new RuntimeException (e);
        }
    }
}
