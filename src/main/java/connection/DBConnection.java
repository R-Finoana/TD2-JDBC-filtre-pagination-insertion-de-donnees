package connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final Dotenv dotenv = Dotenv.configure().load();

    String jdbcUrl = dotenv.get("jdbcUrl");
    String username = dotenv.get("username");
    String password = dotenv.get("password");

    public Connection getDBConnection(){
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e){
            throw new RuntimeException (e);
        }
    }
}
